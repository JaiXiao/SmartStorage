#include "OSAL.h"
#include "AF.h"
#include "ZDApp.h"
#include "ZDObject.h"
#include "ZDProfile.h"
#include <string.h>
//#include "Common.h"
#include "DebugTrace.h"
#include "TempHumApp.h"
#include "MT.h"

#if !defined( WIN32 )
  #include "OnBoard.h"
#endif

/* HAL */
#include "hal_lcd.h"
#include "hal_led.h"
#include "hal_key.h"
#include "hal_uart.h"
#include "mt_uart.h"


#define SEND_DATA_EVENT 0x01

const cId_t TempHumApp_ClusterList[TempHumApp_MAX_CLUSTERS] =
{
  TempHumApp_CLUSTERID
};



const SimpleDescriptionFormat_t TempHumApp_SimpleDesc =
{
  TempHumApp_ENDPOINT,              //  int Endpoint;
  TempHumApp_PROFID,                //  uint16 AppProfId[2];
  TempHumApp_DEVICEID,              //  uint16 AppDeviceId[2];
  TempHumApp_DEVICE_VERSION,        //  int   AppDevVer:4;
  TempHumApp_FLAGS,                 //  int   AppFlags:4;
  
  
  0,          //  byte  AppNumInClusters;
  (cId_t *)NULL,  //  byte *pAppInClusterList;
  TempHumApp_MAX_CLUSTERS,          //  byte  AppNumInClusters;
  (cId_t *)TempHumApp_ClusterList   //  byte *pAppInClusterList;
};

unsigned char TempDATA;
endPointDesc_t TempHumApp_epDesc;
byte TempHumApp_TaskID;
byte TempHumApp_TransID;
devStates_t TempHumApp_NwkState;
void TempHumApp_MessageMSGCB(afIncomingMSGPacket_t *MSGpkt);
void TempHumApp_SendTheMessage(void);



void TempHumApp_Init( byte task_id )
{
  halUARTCfg_t uartConfig;//¥Æø�?    
  TempHumApp_TaskID = task_id;
  TempHumApp_NwkState=DEV_INIT;
  TempHumApp_TransID = 0;

  
  TempHumApp_epDesc.endPoint = TempHumApp_ENDPOINT;
  TempHumApp_epDesc.task_id = &TempHumApp_TaskID;
  TempHumApp_epDesc.simpleDesc
            = (SimpleDescriptionFormat_t *)&TempHumApp_SimpleDesc;
  
  TempHumApp_epDesc.latencyReq = noLatencyReqs;
  afRegister( &TempHumApp_epDesc ); 

}
//增加代码--------------------------------------------------------------------------------------------------------
UINT16 TempHumApp_ProcessEvent( byte task_id, UINT16 events )
{
    afIncomingMSGPacket_t *MSGpkt;

  if ( events & SYS_EVENT_MSG )
  {
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( TempHumApp_TaskID );
    while ( MSGpkt )
    {
      switch ( MSGpkt->hdr.event )
      {
       
          case ZDO_STATE_CHANGE:
            TempHumApp_NwkState = (devStates_t)(MSGpkt->hdr.status);
            if(TempHumApp_NwkState==DEV_END_DEVICE)
            {
              P1_0=~P1_0;
              osal_set_event(TempHumApp_TaskID,SEND_DATA_EVENT);
            }
            break;
  //处理协调结点发送过来的信息--------------------------------------------------------------------------------------
           case AF_INCOMING_MSG_CMD:
             TempHumApp_MessageMSGCB( MSGpkt );//–¬º”µƒ–≈œ¢Ω” ’∫Ø ˝
             break;
        
           default:
             break;
      }
      osal_msg_deallocate( (uint8 *)MSGpkt );
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( TempHumApp_TaskID );
    }
    // return unprocessed events
    return (events ^ SYS_EVENT_MSG);
  }
  if(events&SEND_DATA_EVENT)
  {
    Read_DHT11();
    TempHumApp_SendTheMessage();
    osal_start_timerEx(TempHumApp_TaskID,SEND_DATA_EVENT,3000);
    return(events^SEND_DATA_EVENT);
  }
  return 0;
}

//Ω” ’µΩœ˚œ¢Ω¯––¥¶¿Ìµƒ∫Ø ˝
//处理协调结点发送过来的信息-（从Double.c文件中拷贝的函数�?------------------------------------------------------------
void TempHumApp_MessageMSGCB( afIncomingMSGPacket_t *pkt )
{
  unsigned char buffer[4]; 
  switch ( pkt->clusterId )
  {
  case Double_CLUSTERID:
    osal_memcpy(buffer, pkt->cmd.Data, 4);
    if(buffer[0] == '1')       
    {
      HalLedSet ( HAL_LED_1, HAL_LED_MODE_ON );;
      HalLedSet ( HAL_LED_2, HAL_LED_MODE_ON );;
      HalLedSet ( HAL_LED_3, HAL_LED_MODE_ON );;
      HalLedSet ( HAL_LED_4, HAL_LED_MODE_ON );;
    }
    if(buffer[0] == '2')       
    {
      HalLedSet ( HAL_LED_1, HAL_LED_MODE_OFF );;
      HalLedSet ( HAL_LED_2, HAL_LED_MODE_OFF );;
      HalLedSet ( HAL_LED_3, HAL_LED_MODE_OFF );;
      HalLedSet ( HAL_LED_4, HAL_LED_MODE_OFF );;
    }
    break;
  }
}

void TempHumApp_SendTheMessage(void)
{ 
unsigned char theMessageData[10]="EndDevice";

afAddrType_t my_DstAddr;

my_DstAddr.addrMode=(afAddrMode_t)Addr16Bit;
my_DstAddr.endPoint=TempHumApp_ENDPOINT;
my_DstAddr.addr.shortAddr=0x0000; 


theMessageData[0]=Temp;
theMessageData[1]=Hum;

AF_DataRequest(&my_DstAddr
,&TempHumApp_epDesc
,TempHumApp_CLUSTERID
,osal_strlen("EndDevice")+1
,theMessageData
,&TempHumApp_TransID
,AF_DISCV_ROUTE
,AF_DEFAULT_RADIUS);
}
/*HAL_ISR_FUNCTION( halKeyPort0Isr, P0INT_VECTOR )
{
  
  if (HAL_KEY_SW_7_PXIFG & HAL_KEY_SW_7_BIT)
  {
  }
  //HAL_KEY_SW_7_PXIFG = 0;
  //HAL_KEY_CPU_PORT_0_IF = 0;
}*/


