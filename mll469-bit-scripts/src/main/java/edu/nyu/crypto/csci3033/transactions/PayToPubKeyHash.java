package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;

import static org.bitcoinj.script.ScriptOpCodes.*;
import static org.bitcoinj.script.ScriptOpCodes.OP_VERIFY;

/**
 * Created by bbuenz on 24.09.15.
 */
public class PayToPubKeyHash extends ScriptTransaction {
    // TODO: Problem 1

    ECKey key = randKey();

    public PayToPubKeyHash(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a P2PKH script
        // TODO: be sure to test this script on the mainnet using a vanity address

        ScriptBuilder bld = new ScriptBuilder(); // New builder.

        // Time for the operations! We are going to make a run-of-the-mill, typical p2pkh scriptPubKey.

        // Duplicate to top of stack.
        bld.op(OP_DUP); 

        // Hash the top of the stack.
        bld.op(OP_HASH160); 

        // Push the public key atop the stack.
        bld.data(key.getPubKeyHash()); 

        // Program keeps running if the two top values (hashed item and hashed pub key) are equal. 
        bld.op(OP_EQUALVERIFY); 

        // Checks that the signature (scriptSig) is valid for this whole tx. 
        bld.op(OP_CHECKSIG); 

        // Classical transaction complete. 

        return null;
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // TODO: Redeem the P2PKH transaction

        TransactionSignature txSig = sign(unsignedTransaction, key);
            ScriptBuilder builder = new ScriptBuilder();
            builder.data(txSig.encodeToBitcoin()).data(key.getPubKey);
            return builder.build();
        return null;
    }
}
