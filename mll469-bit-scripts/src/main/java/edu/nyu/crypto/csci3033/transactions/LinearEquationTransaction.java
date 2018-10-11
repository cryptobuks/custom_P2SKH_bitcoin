package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.TransactionSignature;
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
    private DeterministicKey key;

    public LinearEquationTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
        key = getWallet().freshReceiveKey();
    }

    public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
    return data;
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


            // Therefore, correct scriptSig will be <3629> <1980> (in byte form), pushing these two values onto the stack (prepended, concatenated in front)
            // This script is under an assumption that negative values will undergo abs() for clarity. Despite this choice, conceptually it is the same that the scriptSig must be precisely known. 
            // Stack:
            // 3629 (hex is 0E2D)
            // 1980 (hex of abs is 07BC)
            
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

                SUCCESS - 


            */
            
            bld.op(OP_NEGATE);

            bld.op(OP_2DUP);
            bld.op(OP_ADD);
            byte[] r1649 = hexStringToByteArray("7106");
            bld.data(r1649); // 1649 dec to hex is 0x671. Since bitcoin is little-endian, we reverse to order to 7106.  
            bld.op(OP_EQUALVERIFY);
            bld.op(OP_SUB);
            byte[] r5609 = hexStringToByteArray("E915"); // 5609 dec is 0x15E9. Little endian to E915. 
            bld.data(r5609);
            bld.op(OP_EQUALVERIFY);

            // See above for explanation!
        
        return bld.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedScript) {
        // TODO: Create a spending script

        TransactionSignature txSig = sign(unsignedScript, key);
        ScriptBuilder builder = new ScriptBuilder();

            // 3629 (hex is 0E2D)
            // 1980 (hex is 07BC)
        byte[] r3629 = hexStringToByteArray("2D0E"); // little endian
        byte[] r1980 = hexStringToByteArray("BC07");

       builder.data(r3629); // This will be the "scriptSig"
       builder.data(r1980); // just the values x, y [abs(y)]

        return builder.build();
    }

    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
}
