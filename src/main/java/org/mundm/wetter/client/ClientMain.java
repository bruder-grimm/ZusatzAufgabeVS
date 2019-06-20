package org.mundm.wetter.client;

public class ClientMain {

    public static void main(String[] args) {

        Client client = new Client();
        try{
            client.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
