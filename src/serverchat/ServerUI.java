/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package serverchat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author ADMIN
 */
public class ServerUI extends javax.swing.JFrame {

    ServerSocket server;
    HashMap ListClient = new HashMap();
    ArrayList<Account> list = new ArrayList();


    /**
     * Creates new form ServerUI
     */
    public ServerUI() {

        try{
            initComponents();
            ReadListAccount("D:\\LoginAccount.txt");
            this.ServerLabel.setText("ALIVE");
            server = new ServerSocket(2306);
            //ServerLabel = new JLabel("ALIVE");
            new ClientAccept().start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    class Account{
        String username;
        String password;

        public Account(){
            this.username = null;
            this.password = null;
        }

        public Account(String username, String password){
            this.username = username;
            this.password = password;
        }

        public Account(Account a){
            this.username = a.username;
            this.password = a.password;
        }
    }

    class ClientAccept extends Thread{
        public void run(){
            DataInputStream input;
            DataOutputStream output;
            String login = null;
            for(;;){
                try{
                    Socket client = server.accept();
                    input = new DataInputStream(client.getInputStream());
                    output = new DataOutputStream(client.getOutputStream());

                    // Login
                    String username = input.readUTF();
                    String password = input.readUTF();
                    if(Login(username,password) == 1){
                        output.writeUTF("LoginSuccess");
                        String ID = input.readUTF();
                        ListClient.put(username, client);
                        ClientTextArea.append(username + " connected" + "\n");
                        new ClientRead(client, ID).start();
                        new ClientWrite().start();
                    }
                    else if (Login(username,password) == 2){
                        output.writeUTF("WrongPassword");
                    }
                    else{
                        output.writeUTF("LoginFalse");
                    }

                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    public void ReadListAccount(String filename) throws FileNotFoundException, IOException {
        this.list.clear();
        Account a;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        // Read line from input.txt file
        while ((line = reader.readLine()) != null) {
            // Solve the string
            String[] info = line.split("-");
            if(info.length == 2){
                a = new Account(info[0], info[1]);
                this.list.add(a);
            }
        }
        reader.close();
    }
    public int Login(String username, String password) throws IOException {
        ReadListAccount("D:\\LoginAccount.txt");
        for (Account i : this.list) {
            if (i.username.equals(username) && i.password.equals(password)) {
                return 1;
            }
            if(i.username.equals(username) && i.password.equals(password) == false){
                return 2;
            }
        }
        return 0;
    }

    class ClientRead extends Thread {
        Socket client;
        String ID;

        public ClientRead(Socket client, String ID){
            this.client = client;
            this.ID = ID;
        }
        public void run() {
            while (!ListClient.isEmpty()) {
                try {
                    DataInputStream input = new DataInputStream(client.getInputStream());
                    String rmessage = input.readUTF();
                    if (rmessage.equals("CLIENTQUIT")) {
                        Set k = ListClient.keySet();
                        Iterator iter = k.iterator();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            if (!key.equalsIgnoreCase(ID)) {
                                try {
                                    DataOutputStream output = new DataOutputStream(((Socket) ListClient.get(key)).getOutputStream());
                                    output.writeUTF(ID + " " + rmessage);
                                } catch (Exception ex) {
                                    ListClient.remove(ID);
                                    ClientTextArea.append(ID + "quited " + "\n");
                                    new ClientWrite().start();
                                }
                            }
                        }
                    }
                    else{
                        Set k = ListClient.keySet();
                        Iterator iter = k.iterator();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            if (!key.equalsIgnoreCase(ID)) {
                                try {
                                    DataOutputStream output = new DataOutputStream(((Socket) ListClient.get(key)).getOutputStream());
                                    output.writeUTF(ID + ": " + rmessage);
                                } catch (Exception ex) {
                                    ListClient.remove(ID);
                                    ClientTextArea.append(ID + "quited " + "\n");
                                    new ClientWrite().start();
                                }
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ClientWrite extends Thread{

        public void run(){
            try{
                String ID = "";
                Set k = ListClient.keySet();
                Iterator iter = k.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    ID += key +",";
                }
                if(ID.length() != 0)
                    ID = ID.substring(0,ID.length()-1);
                iter = k.iterator();
                while(iter.hasNext()){
                    String key = (String)iter.next();
                    try{
                        DataOutputStream output = new DataOutputStream(((Socket) ListClient.get(key)).getOutputStream());
                        output.writeUTF("USERONLINE" + ID);
                    }catch(Exception ex){
                        ListClient.remove(key);
                        ClientTextArea.append(key + "Quit" + "\n");
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ClientTextArea = new javax.swing.JTextArea();
        ServerLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 255, 255));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 18)); // NOI18N
        jLabel2.setText("Status");

        ClientTextArea.setColumns(20);
        ClientTextArea.setRows(5);
        jScrollPane1.setViewportView(ClientTextArea);

        ServerLabel.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ServerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ServerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ServerUI().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JTextArea ClientTextArea;
    private javax.swing.JLabel ServerLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration
}
