package pbftSimulator.replica;

// import java.util.UUID;
// import pbftSimulator.message.Transaction;
import pbftSimulator.message.RequestMsg;

// Replica r1 = new ByztReplica(1, ByztReplica.ByzantineBehaviorType.INVALID_SIGNATURE);
// Replica r2 = new ByztReplica(2, ByztReplica.ByzantineBehaviorType.DUPLICATE_TX);
// Replica r3 = new Replica(3)
public class ByztReplica extends Replica {
	
	public static final String BTZTPROCESSTAG = "BtztProcess";
	
	public static final String BTZTRECEIVETAG = "BtztReceive";
	
	public static final String BTZTSENDTAG = "BtztSend";
	
	public enum ByzantineBehaviorType {
    		INVALID_TX_STRUCTURE,
    		INVALID_SIGNATURE,
    		INVALID_HASH,
    		DUPLICATE_TX,
    		INVALID_QUOTA,
    		AMOUNT_OVERFLOW,
    		AMOUNT_UNDERFLOW
	}

	// public ByztReplica(int id, int[] netDlys, int[]netDlysToClis) {
	// 	super(id, netDlys, netDlysToClis);
	//	receiveTag = BTZTRECEIVETAG;
	//	sendTag = BTZTSENDTAG;
	// }
    private ByzantineBehaviorType behaviorType;

    public ByztReplica(int id, int[] netDlys, int[] netDlysToClis,
                       ByzantineBehaviorType behaviorType) {
        super(id, netDlys, netDlysToClis);
        receiveTag = BTZTRECEIVETAG;
        sendTag = BTZTSENDTAG;
        this.behaviorType = behaviorType;
    }
    //public ByztReplica(int id, ByzantineBehaviorType behaviorType) {
    //     super(id);
    //    this.behaviorType = behaviorType;
    //}

    @Override
    public void handle(RequestMsg msg) {
        switch (behaviorType) {
            case INVALID_STRUCTURE:
                injectInvalidStructure(msg);
                break;
            case FAKE_CLIENTID:
                injectFakeClientId(msg);
                break;
            case INVALID_TIMESTAMP:
                injectInvalidTimestamp(msg);
                break;
            case DUPLICATE_REQUEST:
                injectDuplicateRequest(msg);
                return; // 不再继续
            case NEGATIVE_TIMESTAMP:
                injectNegativeTimestamp(msg);
                break;
            case TIMESTAMP_OVERFLOW:
                injectOverflowTimestamp(msg);
                break;
            case NONE:
            default:
                break;
        }
        super.handle(msg);
    }

    // ========== 各种攻击函数 ==========

    /** 无效结构: 设置操作字段为 null 
     *  影响: 其他副本在验证请求时可能报错或拒绝执行 
     */
    private void injectInvalidStructure(RequestMsg msg) {
        System.out.println("[Byzantine] Invalid structure (o=null)");
        msg.o = null;
    }

    /** 伪造客户端 ID
     *  影响: 可能导致副本错误归属请求，破坏客户端身份一致性 
     */
    private void injectFakeClientId(RequestMsg msg) {
        System.out.println("[Byzantine] Fake client id");
        msg.c = -999;
    }

    /** 篡改时间戳
     *  影响: 可能破坏请求排序，导致不同副本对请求顺序判断不一致 
     */
    private void injectInvalidTimestamp(RequestMsg msg) {
        System.out.println("[Byzantine] Invalid timestamp");
        msg.t = 123456789; // 任意篡改
    }

    /** 重复请求
     *  影响: 副本可能多次执行同一操作，破坏幂等性或一致性 
     */
    private void injectDuplicateRequest(RequestMsg msg) {
        System.out.println("[Byzantine] Duplicate request");
        super.handle(msg);
        super.handle(msg);
    }

    /** 时间戳为负
     *  影响: 违反系统时间约束，可能被认为是非法请求 
     */
    private void injectNegativeTimestamp(RequestMsg msg) {
        System.out.println("[Byzantine] Negative timestamp");
        msg.t = -1;
    }

    /** 时间戳溢出
     *  影响: 可能导致比较逻辑异常（例如排序或过期检查） 
     */
    private void injectOverflowTimestamp(RequestMsg msg) {
        System.out.println("[Byzantine] Overflow timestamp");
        msg.t = Long.MAX_VALUE;
    }
	
}
