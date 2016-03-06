package app;
// test
public class StudentNetworkSimulator extends NetworkSimulator
{

    private int seqNum;
    private Packet lastPacket;
    //private int ackNum;
    //private boolean isWaiting;
    private boolean lastMessageAckd;

    
    private Message lastMessage;
    //private double timeout;
    //private int ackB;
    
    /*
     * Predefined Constants (static member variables):
     *
     *   int MAXDATASIZE : the maximum size of the Message data and
     *                     Packet payload
     *
     *   int A           : a predefined integer that represents entity A
     *   int B           : a predefined integer that represents entity B
     *
     *
     * Predefined Member Methods:
     *
     *  void stopTimer(int entity): 
     *       Stops the timer running at "entity" [A or B]
     *  void startTimer(int entity, double increment): 
     *       Starts a timer running at "entity" [A or B], which will expire in
     *       "increment" time units, causing the interrupt handler to be
     *       called.  You should only call this with A.
     *  void toLayer3(int callingEntity, Packet p)
     *       Puts the packet "p" into the network from "callingEntity" [A or B]
     *  void toLayer5(int entity, String dataSent)
     *       Passes "dataSent" up to layer 5 from "entity" [A or B]
     *  double getTime()
     *       Returns the current time in the simulator.  Might be useful for
     *       debugging.
     *  void printEventList()
     *       Prints the current event list to stdout.  Might be useful for
     *       debugging, but probably not.
     *
     *
     *  Predefined Classes:
     *
     *  Message: Used to encapsulate a message coming from layer 5
     *    Constructor:
     *      Message(String inputData): 
     *          creates a new Message containing "inputData"
     *    Methods:
     *      boolean setData(String inputData):
     *          sets an existing Message's data to "inputData"
     *          returns true on success, false otherwise
     *      String getData():
     *          returns the data contained in the message
     *  Packet: Used to encapsulate a packet
     *    Constructors:
     *      Packet (Packet p):
     *          creates a new Packet that is a copy of "p"
     *      Packet (int seq, int ack, int check, String newPayload)
     *          creates a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and a
     *          payload of "newPayload"
     *      Packet (int seq, int ack, int check)
     *          chreate a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and
     *          an empty payload
     *    Methods:
     *      boolean setSeqnum(int n)
     *          sets the Packet's sequence field to "n"
     *          returns true on success, false otherwise
     *      boolean setAcknum(int n)
     *          sets the Packet's ack field to "n"
     *          returns true on success, false otherwise
     *      boolean setChecksum(int n)
     *          sets the Packet's checksum to "n"
     *          returns true on success, false otherwise
     *      boolean setPayload(String newPayload)
     *          sets the Packet's payload to "newPayload"
     *          returns true on success, false otherwise
     *      int getSeqnum()
     *          returns the contents of the Packet's sequence field
     *      int getAcknum()
     *          returns the contents of the Packet's ack field
     *      int getChecksum()
     *          returns the checksum of the Packet
     *      String getPayload()
     *          returns the Packet's payload
     *
     */

    // Add any necessary class variables here.  Remember, you cannot use
    // these variables to send messages error free!  They can only hold
    // state information for A or B.
    // Also add any necessary methods (e.g. checksum of a String)


    // This is the constructor.  Don't touch!
    public StudentNetworkSimulator(int numMessages,
                                   double loss,
                                   double corrupt,
                                   double avgDelay,
                                   int trace,
                                   long seed)
    {
        super(numMessages, loss, corrupt, avgDelay, trace, seed);
    }

    // This routine will be called whenever the upper layer at the sender [A]
    // has a message to send.  It is the job of your protocol to insure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving upper layer. Return 1 if accepting the message to send, 
    // return 0 if refusing to send the message
    @Override
    protected int aOutput(Message message)
    {
        
//        if (message.getData() != lastMessage.getData()) {
//            seqNum++;
//        } 
        String temp = message.getData();
        int checksum = calChecksum(temp);
        //int currentSeqNum = 0;
        
        if (lastMessageAckd = true) {
            Packet p = new Packet(seqNum,0,checksum,message.getData());
            lastPacket = p;
            lastMessage = message;
            toLayer3(A,p);
            seqNum++;
            lastMessageAckd = false;
        } else {
            toLayer3(A,lastPacket);
        }
        //startTimer(A,timeout);
        
        return 1;
    }
    
    // This routine will be called whenever a packet sent from the A-side 
    // (i.e. as a result of a toLayer3() being done by an A-side procedure)
    // arrives at the B-side.  "packet" is the (possibly corrupted) packet
    // sent from the A-side.
    protected void bInput(Packet packet)
    {
    	//***GETTING STARTED***
    	// To get started, extract the payload from the packet
    	// and then send it up toLayer5
        
        String temp = new String(packet.getPayload());
        //Message message = new Message(temp);
        
        int checksum = calChecksum(temp);
        
        
        
        if (checksum == packet.getChecksum()) {
            System.out.println("yo: Received checksum: " + packet.getChecksum() + ". Calculated checksum: " + checksum + ". Received payload: " + packet.getPayload());
            toLayer5(B,new Message(packet.getPayload()));
            Packet p = new Packet(0,1,0);
            toLayer3(B,p);
        } else {
            System.out.println("yo: Received checksum: " + packet.getChecksum() + ". Calculated checksum: " + checksum + ". Received payload: " + packet.getPayload());
            Packet p = new Packet(0,0,0);
            toLayer3(B,p);
        }
    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by a B-side procedure)
    // arrives at the A-side.  "packet" is the (possibly corrupted) packet
    // sent from the B-side.
    protected void aInput(Packet packet)
    {
    	//***GETTING STARTED***
    	// This will be needed later, when dealing with acknowledgments sent from B
        //stopTimer(A);
        
        Packet p = packet;
        
        if (p.getAcknum() == 1) {
            lastMessageAckd = true;
            toLayer5(A,"ACKED");
        } else if (p.getAcknum() == 0) { // && p.getPayload().equalsIgnoreCase("")
            lastMessageAckd = false;
            aOutput(lastMessage);
            toLayer5(A,"NACKED. RESENDING PACKETS");
        }
        
    	
    }
    
    // This routine will be called when A's timer expires (thus generating a 
    // timer interrupt). You'll probably want to use this routine to control 
    // the retransmission of packets. See startTimer() and stopTimer(), above,
    // for how the timer is started and stopped. 
    protected void aTimerInterrupt()
    {
    	//***GETTING STARTED***
    	// This will be needed later, to deal with lost packets
        toLayer5(A,"hello");
    }
    
    // This routine will be called once, before any of your other A-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity A).
    protected void aInit()
    {
    	//***GETTING STARTED***
    	// This will be needed later
        
        //isWaiting = false;
        seqNum = 0;
        //timeout = 400;
        //ackNum = 0;
        lastMessageAckd = true;
    }
    
    
    
    // This routine will be called once, before any of your other B-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity B).
    protected void bInit()
    {
    	//***GETTING STARTED***
    	// This will be needed later
    }
    
//    protected int calChecksum(Message message) {
//        int checksum = 0;
//        
//        for (int i = 0; i < message.getData().length(); i++) {
//            checksum += message.getData().charAt(i);
//        }
//        
//        return checksum;
//    }
    
    protected int calChecksum(String message) {
        int checksum = 0;
        
        for (int i = 0; i < message.length(); i++) {
            checksum += message.charAt(i);
        }
        
        return checksum;
    }
}