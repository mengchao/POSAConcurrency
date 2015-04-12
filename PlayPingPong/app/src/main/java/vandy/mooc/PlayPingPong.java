package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    // @@ TODO - you fill in here.
    final private Handler[] mPingPongHandler = new Handler[2];

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    // @@ TODO - you fill in here.
    private CyclicBarrier mBarrier = new CyclicBarrier(mPingPongHandler.length);

    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            // @@ TODO - you fill in here.
            mMyType = myType;
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - you fill in here.
            mPingPongHandler[mMyType.ordinal()] = new Handler(this);

            int count = 0;

            try {
                // Wait for both Threads to initialize their Handlers.
                // @@ TODO - you fill in here.
                count = mBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            // @@ TODO - you fill in here.
            if (count == 0) {
                Handler pingHandler = mPingPongHandler[0];
                Handler pongHandler = mPingPongHandler[1];
                Message msg = pingHandler.obtainMessage();
                msg.setTarget(pingHandler);
                msg.obj = pongHandler;
                pingHandler.sendMessage(msg);
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.
            Handler thisHandler = reqMsg.getTarget();

            if (mIterationsCompleted < mMaxIterations) {
                mOutputStrategy.print(String.format("%s(%d)\n",
                        mMyType.toString(), mIterationsCompleted + 1));
            } else {
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
                Looper.myLooper().quit();
            }

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.
            Handler responseHandler = (Handler) reqMsg.obj;
            Message replyMsg = responseHandler.obtainMessage();
            replyMsg.setTarget(responseHandler);
            replyMsg.obj = thisHandler;

            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - you fill in here.
            responseHandler.sendMessage(replyMsg);

            ++mIterationsCompleted;

            return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!\n");
       
        // Create the ping and pong threads.
        // @@ TODO - you fill in here.
        Thread[] pingpongThreads = new Thread[2];
        pingpongThreads[0] = new PingPongThread(PingPong.PING);
        pingpongThreads[1] = new PingPongThread(PingPong.PONG);

        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - you fill in here.
        pingpongThreads[0].start();
        pingpongThreads[1].start();

        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        // @@ TODO - you fill in here.
        for (Thread thread : pingpongThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
