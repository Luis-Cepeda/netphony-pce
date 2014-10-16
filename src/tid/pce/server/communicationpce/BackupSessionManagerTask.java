package tid.pce.server.communicationpce;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import es.tid.pce.pcep.messages.PCEPMessage;
import tid.pce.pcepsession.PCEPSessionsInformation;
import tid.pce.server.NotificationDispatcher;
import tid.pce.server.PCEServerParameters;
import tid.pce.tedb.TEDB;

public class BackupSessionManagerTask  extends TimerTask{

	private PCEServerParameters params;
	
	private BackupPCESession backupPCESession;

	private TEDB ted;

	private Logger log;
	private LinkedBlockingQueue<PCEPMessage> sendingQueue;
	 NotificationDispatcher notificationDispatcher;
	CollaborationPCESessionManager collaborationPCESessionManager;
	PCEPSessionsInformation pcepSessionInformation;
	
	public BackupSessionManagerTask(PCEServerParameters params, TEDB ted,CollaborationPCESessionManager collaborationPCESessionManager, NotificationDispatcher notificationDispatcher,PCEPSessionsInformation pcepSessionInformation){		
		this.params=params;  
		this.ted=ted;
		log=Logger.getLogger("PCEServer");
		this.pcepSessionInformation=pcepSessionInformation;
		//this.backupPCENotificationQueue=new RequestQueue(params.getParentPCERequestProcessors());
		this.collaborationPCESessionManager= collaborationPCESessionManager;
		this.notificationDispatcher=notificationDispatcher;
		sendingQueue=new LinkedBlockingQueue<PCEPMessage>();
	}


	/**
	 * Starts new session with backup PCE
	 * 
	 * @return true if the session was launched, false if the session was already established
	 */
	public void run(){
		if(backupPCESession != null){
			if (backupPCESession.isAlive()){
				if (backupPCESession.isInterrupted()){
					log.severe("THREAD VIVO... SESION DE BACKUP MUERTA");
				
				}
				return;	
			}
			else{
				log.severe("Session with backup PCE dead, trying to establish new session");
				Timer timer=new Timer();
				backupPCESession= new BackupPCESession(params.getIpPrimaryPCE(),params.getPortPrimaryPCE(),params.isNodelay(),ted,collaborationPCESessionManager,notificationDispatcher,timer, pcepSessionInformation/*, params.getLocalPceAddress(), params.getPCEServerPort()*/);
				backupPCESession.start();
			
				return;
			}
		}else{
			log.severe("No Session with backup PCE, trying to establish new session");
			Timer timer=new Timer();
			backupPCESession= new BackupPCESession(params.getIpPrimaryPCE(),params.getPortPrimaryPCE(),params.isNodelay(),ted,collaborationPCESessionManager,notificationDispatcher,timer,pcepSessionInformation/*, params.getLocalPceAddress(), params.getPCEServerPort()*/);
			backupPCESession.start();
			log.severe("Adding a new session in sessionManagerList");
			
			return;
		}

	   
	}
	
	public boolean PPCESessionStatus(){
		return backupPCESession.isAlive();
	}
	
	public int PPCESessionState(){
		return backupPCESession.getFSMstate();
	}
	
//	public void killPPCESession(){
//		
//		backupPCESession.killSession();
//	}
	
//	public void closePPceSession(){
//		if (backupPCESession.isAlive()){
//			//FIXME reason for close????
//			backupPCESession.close(1);
//		}
//		
//	}
	
}
