package com.example.s94285.tcptest1;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;

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

class ModbusRW{
    private ModbusMaster modbusMaster;
    private final int SLAVE_ID = 1;
    private Exception mbNotInitialized = new Exception("Specific Modbus Master isn't initialized");

    ModbusRW(ModbusMaster modbusMaster){
        this.modbusMaster = modbusMaster;
    }


    /**Reading Values***************************************************************************/

    Boolean[] mbReadByteToBoolean(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        Boolean[] mb = new Boolean[8];
        if(!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        }else{
            for(byte bitLocation = 0;bitLocation<8;bitLocation++){
                mb[bitLocation] = modbusMaster.getValue(SLAVE_ID,range,offset,bitLocation);
            }
        }
        return mb;
    }

    Boolean[] mbReadWordToBolean(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        Boolean[] mb = new Boolean[16];
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
    Integer mbReadINTToInteger(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        Short mb;
        if(!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        }else{
            mb = (Short)modbusMaster.getValue(SLAVE_ID,range,offset,DataType.TWO_BYTE_INT_SIGNED);
        }
        return Integer.parseInt(mb.toString());
    }

    Integer mbReadDINTToInteger(int offset) throws Exception {
        int range = RegisterRange.HOLDING_REGISTER;
        int mb;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Integer) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.FOUR_BYTE_INT_SIGNED);
        }
        return mb;
    }

    Float mbReadREALToFloat(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        float mb;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Float) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.FOUR_BYTE_FLOAT);
        }
        return mb;
    }

    Double mbReadLREALToDouble(int offset) throws Exception{
        int range = RegisterRange.HOLDING_REGISTER;
        double mb;
        if (!modbusMaster.isInitialized()) {
            throw mbNotInitialized;
        } else {
            mb = (Double) modbusMaster.getValue(SLAVE_ID, range, offset, DataType.EIGHT_BYTE_FLOAT);
        }
        return mb;
    }


    /**Writing Values************************************************************************/


    void mbWriteBooleanToBit(int offset, int bit, boolean bool) throws Exception{
        byte Bit = Byte.parseByte(String.valueOf(bit));
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,Bit,bool);
        }
    }

    void mbWriteBoolArrayToByte(int offset, Boolean[] booleans) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER, offset,DataType.BINARY,boolArrayToByte(booleans));
        }
    }

    void mbWriteBoolArrayToWord(int offset, Boolean[] booleans) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.TWO_BYTE_INT_UNSIGNED,boolArrayToShort(booleans));
        }
    }

    void mbWriteShortToINT(int offset, short input) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.TWO_BYTE_INT_SIGNED,input);
        }
    }

    void mbWriteIntToDINT(int offset, int input) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.FOUR_BYTE_INT_SIGNED,input);
        }
    }

    void mbWriteFloatToReal(int offset, float input) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.FOUR_BYTE_FLOAT,input);
        }
    }

    void mbWriteDoubleToLREAL(int offset, double input) throws Exception{
        if (!modbusMaster.isInitialized()){
            throw mbNotInitialized;
        } else {
            modbusMaster.setValue(SLAVE_ID, RegisterRange.HOLDING_REGISTER,offset,DataType.EIGHT_BYTE_FLOAT,input);
        }
    }

    /********************************************************************************************/

    ModbusMaster getModbusMaster() {
        return modbusMaster;
    }

    void setModbusMaster(ModbusMaster modbusMaster) {
        this.modbusMaster = modbusMaster;
    }

    byte boolArrayToByte(Boolean[] booleenArray){
        int result = 0;
        for(int i = 0; i < 8; i++){
            result += (booleenArray[i]?1:0)<<i;
        }
        return Byte.parseByte(String.valueOf(result));
    }

    short boolArrayToShort(Boolean[] booleenArray){
        int result = 0;
        for(int i = 0; i < 16; i++){
            result += (booleenArray[i]?1:0)<<i;
        }
        return Short.parseShort(String.valueOf(result));
    }
}
