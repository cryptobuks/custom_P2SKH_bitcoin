package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class MultiSigTransaction extends ScriptTransaction {
    // TODO: Problem 3

    ECKey bank = randKey();
    ECKey party1 = randKey();
    ECKey party2 = randKey();
    ECKey party1999 = randKey();

    List<ECKey> keyList = ImmutableList.of(party1, party2, part1999);

    public MultiSigTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend using signatures from the bank and one of the customers

        ScriptBuilder bld = new ScriptBuilder();

        // Bank is presumed to always be the first signer. 
        bld.op(OP_DUP);
        bld.op(OP_HASH160);
        bld.data(bank.getPubKeyHash());
        bld.op(OP_EQUALVERIFY); // Take it to the bank...

        // Now, for the 1 of 3 other parties requirement...

        bld.op(OP_1);
        bld.data(party1.getPubKey);
        bld.data(party2.getPubKey);
        bld.data(party1999.getPubKey);
        bld.op(OP_3);
        builder.op(OP_CHECKMULTISIG); // One of three? Sure! 

        // We only want 1 / 3, no more, no less. If otherwise, this code can be uncommented. I initially thought it was at least 1. 
        // bld.op(OP_NOTIF); // If the above doesn't return true...
        // bld.op(OP_2);
        // bld.data(party1.getPubKey);
        // bld.data(party2.getPubKey);
        // bld.data(party1999.getPubKey);
        // bld.op(OP_3);
        // builder.op(OP_CHECKMULTISIG); // Two of three? Sure! 
        // bld.op(OP_ENDIF);

        // bld.op(OP_NOTIF); // If the above doesn't return true...
        // bld.op(OP_3);
        // bld.data(party1.getPubKey);
        // bld.data(party2.getPubKey);
        // bld.data(party1999.getPubKey);
        // bld.op(OP_3);
        // builder.op(OP_CHECKMULTISIG); // Three of three? Of course! 
        // bld.op(OP_ENDIF);

        bld.op(OP_VERIFY); // There has to be at least one TRUE on the stack by now, else it's 0 out of 3 and invalid. 

        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // Please be aware of the CHECK_MULTISIG bug!
        // TODO: Create a spending script

        // We will have bank + one partu sign, which is technically valid. 
        TransactionSignature bankSig = sign(unsignedTransaction, bank);
        TransactionSignature p1999Sig = sign(unsignedTransaction, party1999);
        TransactionSignature p2Sig = sign(unsignedTransaction, party2);

        ScriptBuilder builder = new ScriptBuilder();

        builder.op(OP_2); // We need a burner value since CHECK_MULTISIG takes one value too many naturally.
        builder.data(bankSig.encodeToBitcoin());
        // builder.data(p2Sig.encodeToBitcoin());
        builder.data(p1999Sig.encodeToBitcoin());




        return null;
    }
}
