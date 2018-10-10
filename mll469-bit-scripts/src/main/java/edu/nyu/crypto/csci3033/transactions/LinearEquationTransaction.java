package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class LinearEquationTransaction extends ScriptTransaction {
    // TODO: Problem 2

    // Get us a key. 
    ECKey key = randKey();

    public LinearEquationTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend by two numbers x and y such that x+y=first 4 digits of your suid and |x-y|=last 4 digits of your suid (perhaps +1)

        ScriptBuilder bld = new ScriptBuilder(); // (Alice and Bob the builder.)

            // My (Michael's) N-number is 16495609 

            // x + y = 1649, x - y = 5609
            
            /* This is the solution required as instructed. Let's just do the work here. 
                x = 1649 - y ==> 1649 - 2y ==> 5609 ==> 2y = -3960 ==> y = -1980
                x = 5609 + y = 3629
                x=3629, y=-1980 :) 
            */

            // Therefore, correct scriptSig will be <3629> <-1980>, pushing these two values onto the stack (prepended, concatenated in front)
            // Stack:
            // 3629
            // -1980
            
            /* Will list the following scriptPubKey operations here for cleanliness:

                Duplicate the two top stack items to the top of the stack, i.e. assuming a correct answer:  
                    3629
                    -1980
                    3629
                    -1980

                Add the two top stack items (becomes one stack item). 
                   1649 
                   3629
                   -1980

                Push the value <1649>.
                    1649
                    1649 
                    3629
                    -1980

                Top two should be equal. 
                    * VERIFIED & POPPED 
                    3629
                    -1980

                Subtract the two top stack items (becomes one stack item). 
                    5609

                Push the value <5609>.
                    5609
                    5609

                Top two should be equal. 
                    * VERIFIED & POPPED 

                Check the signature of the whole transaction. 


            */
            
            bld.op(OP_2DUP);
            bld.op(OP_ADD);
            bld.data(1649);
            bld.op(OP_EQUALVERIFY);
            bld.op(OP_SUB);
            bld.data(5609);
            bld.op(OP_EQUALVERIFY);
            bld.op(OP_CHECKSIG);

            // See above for explanation!


        
        return null;
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedScript) {
        // TODO: Create a spending script
        return null;
    }

    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
}
