package pbftSimulator.replica;

import java.util.UUID;
import pbftSimulator.message.Transaction;

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

    public ByztReplica(int id, ByzantineBehaviorType behaviorType) {
        super(id);
        this.behaviorType = behaviorType;
    }

	@Override
    public void handleRequest(Transaction tx) {
        switch (behaviorType) {
            case INVALID_TX_STRUCTURE:
                System.out.println("[Byzantine] Sending invalid transaction structure");
                tx.setData(null);
                break;
            case INVALID_SIGNATURE:
                System.out.println("[Byzantine] Tampering transaction signature");
                tx.setSignature("FAKE_SIGNATURE");
                break;
            case INVALID_HASH:
                System.out.println("[Byzantine] Tampering transaction hash");
                tx.setHash(UUID.randomUUID().toString());
                break;
            case DUPLICATE_TX:
                System.out.println("[Byzantine] Sending duplicate transaction");
                super.handleRequest(tx);
                super.handleRequest(tx);
                return;
            case INVALID_QUOTA:
                System.out.println("[Byzantine] Setting invalid quota");
                tx.setQuota(-100);
                break;
            case AMOUNT_OVERFLOW:
                System.out.println("[Byzantine] Overflow transaction amount");
                tx.setAmount(Long.MAX_VALUE);
                break;
            case AMOUNT_UNDERFLOW:
                System.out.println("[Byzantine] Negative transaction amount");
                tx.setAmount(-50);
                break;
            case NONE:
            default:
                break;
        }

        super.handleRequest(tx);
    }
	
}
