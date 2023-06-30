package com.mogen.im.tcp.receiver.process;

public class ProcessFactory {


    private static BaseProcess defaultProcess;

    static {
        defaultProcess = new BaseProcess() {
            @Override
            public void processBefore() {
                ;
            }

            @Override
            public void processAfter() {

            }
        };
    }

    public static BaseProcess getMessageProcess(){
       return defaultProcess;
    }
}
