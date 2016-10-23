package com.example.s94285.tcptest1;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Created by s94285 on 2016/10/14.
 *
 BYTE	0	255	8 bit
 WORD	0	65,535	16 bit
 DWORD	0	4,294,967,295	32 bit
 LWORD	0	2^64-1	64 bit
 SINT	–128	127	8 bit
 USINT	0	255	8 bit
 INT	–32,768	32,767	16 bit
 UINT	0	65,535	16 bit
 DINT	–2,147,483,648	2,147,483,647	32 bit
 UDINT	0	4,294,967,295	32 bit
 LINT	–263	263-1	64 bit
 ULINT	0	2^64-1	64 bit
 REAL 1.401e-45...3.403e+38 32bit
 LREAL 2.2250738585072014e-308...1.7976931348623158e+308    64bit
 *
 *
 * for Modbus Read/Write Request with data convert
 */

public class ModbusRW{
    private ModbusMaster modbusMaster;
    private final int SLAVE_ID = 1;
    private Exception mbNotInitialized = new Exception("Specific Modbus Master isn't initialized");

    public ModbusRW(ModbusMaster modbusMaster){
        this.modbusMaster = modbusMaster;
    }


    /**Reading Values***************************************************************************/

    public boolean[] mbReadBytetoBolean(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        boolean[] mb = new boolean[8];
        if(!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        }else{
            for(byte bitLocation = 0;bitLocation<8;bitLocation++){
                mb[bitLocation] = modbusMaster.getValue(SLAVE_ID,range,offset,bitLocation);
            }
        }
        return mb;
    }

    public boolean[] mbReadWordtoBolean(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        boolean[] mb = new boolean[16];
        if(!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        }else{
            for(byte bitLocation = 0;bitLocation<8;bitLocation++){
                mb[bitLocation] = modbusMaster.getValue(SLAVE_ID,range,offset,bitLocation);
            }
            for(byte bitLocation = 0;bitLocation<8;bitLocation++){
                mb[bitLocation+8] = modbusMaster.getValue(SLAVE_ID,range,offset+1,bitLocation);
            }
            }
        return mb;
    }

    /** coz java's int is in 4 bytes, so I did this to fit PLC's INT (2 bytes)*/
    public Integer mbReadINTtoInteger(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        Short mb = 0;
        if(!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        }else{
            mb = (Short)modbusMaster.getValue(SLAVE_ID,range,offset,DataType.TWO_BYTE_INT_SIGNED);
        }
        return Integer.parseInt(mb.toString());
    }

    public Integer mbReadDINTtoInteger(int offset) throws Exception {
        int range = RegisterRange.HOLDING_REGISTER;
        int mb = 0;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Integer) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.FOUR_BYTE_INT_SIGNED);
        }
        return mb;
    }

    public Float mbReadREALtoFloat(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        float mb = 0;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Float) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.FOUR_BYTE_FLOAT);
        }
        return mb;
    }

    public Double mbReadLREALtoDouble(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        double mb = 0;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Double) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.EIGHT_BYTE_FLOAT);
        }
        return mb;
    }


    /**Writing Values************************************************************************/


    public void mbWriteBooleanToBit(int offset, int bit, boolean bool) throws Exception{
        byte Bit = Byte.parseByte(String.valueOf(bit));
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,Bit,bool);
        }
    }

    public void mbWriteBoolArrayToByteWord(int offset, Boolean[] booleans) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            for(byte bytes = 0; bytes < booleans.length; bytes++)
                modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,bytes,booleans[bytes]);
        }
    }

    public void mbWriteIntToINT(int offset, short INT) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.TWO_BYTE_INT_SIGNED,INT);
        }
    }

    public void mbWriteIntToDINT(int offset, int bool) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.FOUR_BYTE_INT_SIGNED);
        }
    }

    public void mbWriteFloatToReal(int offset, float bool) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.FOUR_BYTE_FLOAT);
        }
    }

    public void mbWriteDoubleToLREAL(int offset, float bool) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.EIGHT_BYTE_FLOAT);
        }
    }

    /********************************************************************************************/

    public ModbusMaster getModbusMaster() {
        return modbusMaster;
    }

    public void setModbusMaster(ModbusMaster modbusMaster) {
        this.modbusMaster = modbusMaster;
    }

}
