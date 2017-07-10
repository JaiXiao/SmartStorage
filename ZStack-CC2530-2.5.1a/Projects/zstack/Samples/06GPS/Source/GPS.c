/******************************************************************************
  Filename:       Gps.c
  Revised:        $Date: 2012-03-07 01:04:58 -0800 (Wed, 07 Mar 2012) $
  Revision:       $Revision: 29656 $

  Description:    Generic Application (no Profile).


  Copyright 2004-2012 Texas Instruments Incorporated. All rights reserved.

  IMPORTANT: Your use of this Software is limited to those specific rights
  granted under the terms of a software license agreement between the user
  who downloaded the software, his/her employer (which must be your employer)
  and Texas Instruments Incorporated (the "License"). You may not use this
  Software unless you agree to abide by the terms of the License. The License
  limits your use, and you acknowledge, that the Software may not be modified,
  copied or distributed unless embedded on a Texas Instruments microcontroller
  or used solely and exclusively in conjunction with a Texas Instruments radio
  frequency transceiver, which is integrated into your product. Other than for
  the foregoing purpose, you may not use, reproduce, copy, prepare derivative
  works of, modify, distribute, perform, display or sell this Software and/or
  its documentation for any purpose.

  YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
  PROVIDED “AS IS?WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
  INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
  NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
  TEXAS INSTRUMENTS OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT,
  NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER
  LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES
  INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE
  OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT
  OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY THIRD PARTIES
  (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.

  Should you have any questions regarding your right to use this Software,
  contact Texas Instruments Incorporated at www.TI.com.
******************************************************************************/

/*********************************************************************
  This application isn't intended to do anything useful, it is
  intended to be a simple example of an application's structure.

  This application sends "Hello World" to another "Generic"
  application every 5 seconds.  The application will also
  receives "Hello World" packets.

  The "Hello World" messages are sent/received as MSG type message.

  This applications doesn't have a profile, so it handles everything
  directly - itself.

  Key control:
    SW1:
    SW2:  initiates end device binding
    SW3:
    SW4:  initiates a match description request
*********************************************************************/

/*********************************************************************
 * INCLUDES
 */
#include "OSAL.h"
#include "AF.h"
#include "ZDApp.h"
#include "ZDObject.h"
#include "ZDProfile.h"

#include "Gps.h"
#include "DebugTrace.h"

#if !defined( WIN32 )
  #include "OnBoard.h"
#endif
#include "MT.h"
#include "mt_uart.h"
/* HAL */
#include "hal_lcd.h"
#include "hal_led.h"
#include "hal_key.h"
#include "hal_uart.h"

/* RTOS */
#if defined( IAR_ARMCM3_LM )
#include "RTOS_App.h"
#endif  
#include<ioCC2530.h>
#include <string.h>
#define uint unsigned int
#define uchar unsigned char
/*********************************************************************
 * MACROS
 */
/*********************************************************************
 * DATA
 */
//char Recdata1[128]="CHENGDU JIAJIE TECH.INC";
//char Recdata2[128]="CHENGDU JIAJIE TECH.INC";
uchar Recdata[300]="CHENGDU JIAJIE TECH.INC";
uchar Recdata_GPGGA[70]="CHENGDU JIAJIE TECH.INC";
uint Recdata_GPGGA_start=0;
uchar Recdata_GPGSV[70]="CHENGDU JIAJIE TECH.INC";
uint Recdata_GPGSV_start=0;
uchar Recdata_GPGSA[70]="CHENGDU JIAJIE TECH.INC";
uint Recdata_GPGSA_start=0;
uchar Recdata_GPRMC[70]="CHENGDU JIAJIE TECH.INC";
uint Recdata_GPRMC_start=0;
uchar Recdata_GPVTG[70]="CHENGDU JIAJIE TECH.INC";
uint Recdata_GPVTG_start=0;
uchar UTC[6]="000000";
uchar JingDu[10]="0000000000";
uchar WeiDu[9]="000000000";
uchar GaoDu[5]="00000";
uchar WeiXing[2]="00";
uchar JiBie[1]="y";
uchar RXTXflag = 1;
uchar temp;
uint  datanumber = 0;
uint  stringlen;
uint check=0;
uint position=6;
/*********************************************************************
 * CONSTANTS
 */

/*********************************************************************
 * TYPEDEFS
 */

/*********************************************************************
 * GLOBAL VARIABLES
 */
// This list should be filled with Application specific Cluster IDs.
#define SEND_DATA_EVENT 0x01
unsigned char flag='0';
const cId_t Gps_ClusterList[Gps_MAX_CLUSTERS] =
{
  Gps_CLUSTERID
};



const SimpleDescriptionFormat_t Gps_SimpleDesc =
{
  Gps_ENDPOINT,              //  int Endpoint;
  Gps_PROFID,                //  uint16 AppProfId[2];
  Gps_DEVICEID,              //  uint16 AppDeviceId[2];
  Gps_DEVICE_VERSION,        //  int   AppDevVer:4;
  Gps_FLAGS,                 //  int   AppFlags:4;
  
  
  0,          //  byte  AppNumInClusters;
  (cId_t *)NULL,  //  byte *pAppInClusterList;
  Gps_MAX_CLUSTERS,          //  byte  AppNumInClusters;
  (cId_t *)Gps_ClusterList   //  byte *pAppInClusterList;
};

unsigned char TempDATA;
endPointDesc_t Gps_epDesc;
byte Gps_TaskID;
byte Gps_TransID;
devStates_t Gps_NwkState;
byte Gps_Uart_TaskID;
byte Gps_Uart_TransID;
void Gps_MessageMSGCB(afIncomingMSGPacket_t *MSGpkt);
void Gps_SendTheMessage(void);
void handle();



void Gps_Init( byte task_id )
{
  halUARTCfg_t uartConfig;//´®¿Ú
    
  Gps_TaskID = task_id;
  Gps_NwkState=DEV_INIT;
  Gps_TransID = 0;

  MT_UartInit();
  //initUARTSEND();
  MT_UartRegisterTaskID(task_id);
  
  Gps_epDesc.endPoint = Gps_ENDPOINT;
  Gps_epDesc.task_id = &Gps_TaskID;
  Gps_epDesc.simpleDesc
            = (SimpleDescriptionFormat_t *)&Gps_SimpleDesc;
  
  Gps_epDesc.latencyReq = noLatencyReqs;
  afRegister( &Gps_epDesc ); 
  uartConfig.configured = TRUE;
  uartConfig.baudRate = HAL_UART_BR_9600;
  uartConfig.flowControl = FALSE;
  //uartConfig.callBackFunc         = rxCB;
  HalUARTOpen(0,&uartConfig);

}

UINT16 Gps_ProcessEvent( byte task_id, UINT16 events )
{
  afIncomingMSGPacket_t *MSGpkt;

  if ( events & SYS_EVENT_MSG )
  {
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( Gps_TaskID );
    while ( MSGpkt )
    {
      switch ( MSGpkt->hdr.event )
      {
       
          case ZDO_STATE_CHANGE:
            Gps_NwkState = (devStates_t)(MSGpkt->hdr.status);
            if(Gps_NwkState==DEV_END_DEVICE)
            {
              P1_0=~P1_0;
              osal_set_event(Gps_TaskID,SEND_DATA_EVENT);
            }
            break;
            
          default:
            break;
      }
      osal_msg_deallocate( (uint8 *)MSGpkt );
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( Gps_TaskID );
    }
    // return unprocessed events
    return (events ^ SYS_EVENT_MSG);
  }
  if(events&SEND_DATA_EVENT)
  {
    //HalUARTRead(0, Recdata, 300);
    //HalUARTRead(0, Recdata2, 128);
    /*for(uint i=0;i<128;i++)
      Recdata[i]=Recdata1[i];
    for(uint i=0;i<128;i++)
      Recdata[i+128]=Recdata2[i];*/
    //handle();
    Gps_SendTheMessage();
    osal_start_timerEx(Gps_TaskID,SEND_DATA_EVENT,Gps_SEND_MSG_TIMEOUT);
    return(events^SEND_DATA_EVENT);
  }
  return 0;
}

void Gps_SendTheMessage(void)
{ 
  unsigned char theMessageData[33] = "Gps:";
  for (unsigned char i=0;i<6;i++)
    theMessageData[i]=UTC[i];
  for (unsigned char i=0;i<9;i++)
    theMessageData[i+6]=WeiDu[i];
  for (unsigned char i=0;i<10;i++)
    theMessageData[i+15]=JingDu[i];
  for (unsigned char i=0;i<1;i++)
    theMessageData[i+25]=JiBie[i];
  for (unsigned char i=0;i<2;i++)
    theMessageData[i+26]=WeiXing[i];
  for (unsigned char i=0;i<5;i++)
    theMessageData[i+28]=GaoDu[i];

  afAddrType_t my_DstAddr;

  my_DstAddr.addrMode=(afAddrMode_t)Addr16Bit;
  my_DstAddr.endPoint=Gps_ENDPOINT;
  my_DstAddr.addr.shortAddr=0x0000; 


  //theMessageData[0]=flag;

  AF_DataRequest(&my_DstAddr
  ,&Gps_epDesc
  ,Gps_CLUSTERID
  ,33
  ,theMessageData
  ,&Gps_TransID
  ,AF_DISCV_ROUTE
  ,AF_DEFAULT_RADIUS);
}
bool isDigital(unsigned char c)
{
  if(c=='0' || c=='1' ||c=='2' ||c=='3' ||c=='4' ||
     c=='5' ||c=='6' ||c=='7' ||c=='8' ||c=='9' || c=='.')
    return true;
  else
    return false;
}
void handle()
{
  check=0;
  Recdata_GPGGA_start=0;
  Recdata_GPGSV_start=0;
  Recdata_GPGSA_start=0;
  Recdata_GPRMC_start=0;
  Recdata_GPVTG_start=0;
  while(check<295)
  {
      if(Recdata[check]=='$'&&Recdata[check+1]=='G'&&Recdata[check+2]=='P'
        &&Recdata[check+3]=='G'&&Recdata[check+4]=='G'&&Recdata[check+5]=='A')
      {
         position=check+6;
         while(Recdata_GPGGA_start<70&&Recdata[position++]!='$')
         {
           Recdata_GPGGA[Recdata_GPGGA_start++]=Recdata[position-1];
           Recdata_GPGSV_start=0;
           Recdata_GPGSA_start=0;
           Recdata_GPRMC_start=0;
           Recdata_GPVTG_start=0;
         }
         //uint GaoDu_count=0;
         uint i_utc=0;
         uint i=0;
         if(Recdata_GPGGA[i_utc+1]!='H')
         {
           while(Recdata_GPGGA[i_utc+1]!=',')
          {
            UTC[i++]=Recdata_GPGGA[i_utc+1];
            i_utc++;
          }
         }
         uint i_weidu=i_utc+1;
         i=0;
         while(Recdata_GPGGA[i_weidu+1]!=',')
         {
           WeiDu[i++]=Recdata_GPGGA[i_weidu+1];
           i_weidu++;
         }
         uint i_N=i_weidu+1;
         i=0;
         while(Recdata_GPGGA[i_N+1]!=',')
         {
           i_N++;
         }
         uint i_JingDu=i_N+1;
         i=0;
         while(Recdata_GPGGA[i_JingDu+1]!=',')
         {
           JingDu[i++]=Recdata_GPGGA[i_JingDu+1];
           i_JingDu++;
         }
         uint i_E=i_JingDu+1;
         i=0;
         while(Recdata_GPGGA[i_E+1]!=',')
         {
           i_E++;
         }
         uint i_temp=i_E+1;
         i=0;
         while(Recdata_GPGGA[i_temp+1]!=',')
         {
           i_temp++;
         }
         uint i_WeiXing=i_temp+1;
         i=0;
         while(Recdata_GPGGA[i_WeiXing+1]!=',')
         {
           WeiXing[i++]=Recdata_GPGGA[i_WeiXing+1];
           i_WeiXing++;
         }
         uint i_W=i_WeiXing+1;
         i=0;
         while(Recdata_GPGGA[i_W+1]!=',')
         {         
           i_W++;
         }
         uint i_GaoDu=i_W+1;
         i=0;
         while(Recdata_GPGGA[i_GaoDu+1]!=',')
         {    
           GaoDu[i++]=Recdata_GPGGA[i_GaoDu+1];
           i_GaoDu++;
         }
         /*for (uint i=0;i<6;i++)
           UTC[i]=Recdata_GPGGA[i+1];
         if (Recdata_GPGGA[12]==',')
         {
           for (uint i=0;i<9;i++)
             WeiDu[i]='*';
           for (uint i=0;i<10;i++)
             JingDu[i]='*';
           for (uint i=0;i<2;i++)
             WeiXing[i]='*';
           for (uint i=0;i<5;i++)
             GaoDu[i]='*';
         }
         else
         {
           for (uint i=0;i<9;i++)
             WeiDu[i]=Recdata_GPGGA[i+12];
           for (uint i=0;i<10;i++)
             JingDu[i]=Recdata_GPGGA[i+24];
         }
         uint GaoDu_count=0;
         while(Recdata_GPGGA[GaoDu_count]!='M')
         {
           if(GaoDu_count<70)
             GaoDu_count++;
         }
         if(Recdata_GPGGA[GaoDu_count]=='M')
         {
           if(Recdata_GPGGA[GaoDu_count-6]>'0'&&Recdata_GPGGA[GaoDu_count-6]<='9')
           {
             GaoDu[0]=Recdata_GPGGA[GaoDu_count-6];
             GaoDu[1]=Recdata_GPGGA[GaoDu_count-5];
             GaoDu[2]=Recdata_GPGGA[GaoDu_count-4];
             GaoDu[3]=Recdata_GPGGA[GaoDu_count-3];
             GaoDu[4]=Recdata_GPGGA[GaoDu_count-2];
           }
           else if(Recdata_GPGGA[GaoDu_count-5]>'0'&&Recdata_GPGGA[GaoDu_count-6]<='9')
           {
             GaoDu[0]='0';
             GaoDu[1]=Recdata_GPGGA[GaoDu_count-5];
             GaoDu[2]=Recdata_GPGGA[GaoDu_count-4];
             GaoDu[3]=Recdata_GPGGA[GaoDu_count-3];
             GaoDu[4]=Recdata_GPGGA[GaoDu_count-2];
           }
           else 
           {
             GaoDu[0]='0';
             GaoDu[1]='0';
             GaoDu[2]=Recdata_GPGGA[GaoDu_count-4];
             GaoDu[3]=Recdata_GPGGA[GaoDu_count-3];
             GaoDu[4]=Recdata_GPGGA[GaoDu_count-2];
           }
         }*/
      }
      else if(Recdata[check]=='$'&&Recdata[check+1]=='G'&&Recdata[check+2]=='P'
         &&Recdata[check+3]=='G'&&Recdata[check+4]=='S'&&Recdata[check+5]=='V')
      {
         position=check+6;
         while(Recdata[position++]!='$'&&Recdata_GPGSV_start<70)
         {
           Recdata_GPGSV[Recdata_GPGSV_start++]=Recdata[position-1];
           Recdata_GPGGA_start=0;
           Recdata_GPGSA_start=0;
           Recdata_GPRMC_start=0;
           Recdata_GPVTG_start=0;
         }
         /*for (uint i=0;i<2;i++)
           WeiXing[i]=Recdata_GPGSV[i+5];*/
      }
      else if(Recdata[check]=='$'&&Recdata[check+1]=='G'&&Recdata[check+2]=='P'
          &&Recdata[check+3]=='G'&&Recdata[check+4]=='S'&&Recdata[check+5]=='A')
      {
          position=check+6;
          while(Recdata_GPGSA_start<70&&Recdata[position++]!='$')
          {
            Recdata_GPGSA[Recdata_GPGSA_start++]=Recdata[position-1];
            Recdata_GPGSV_start=0;
            Recdata_GPGGA_start=0;
            Recdata_GPRMC_start=0;
            Recdata_GPVTG_start=0;
          }
          JiBie[0]=Recdata_GPGSA[3];
      }
      else if(Recdata[check]=='$'&&Recdata[check+1]=='G'&&Recdata[check+2]=='P'
           &&Recdata[check+3]=='R'&&Recdata[check+4]=='M'&&Recdata[check+5]=='C')
      {
          position=check+6;
          while(Recdata_GPRMC_start<70&&Recdata[position++]!='$')
          {
            Recdata_GPRMC[Recdata_GPRMC_start++]=Recdata[position-1];
            Recdata_GPGSV_start=0;
            Recdata_GPGSA_start=0;
            Recdata_GPGGA_start=0;
            Recdata_GPVTG_start=0;
          }
          uint i_utc=0;
          uint i=0;
          while(Recdata_GPGGA[i_utc+1]!=',')
          {
            UTC[i++]=Recdata_GPGGA[i_utc+1];
            i_utc++;
          }
          uint i_temp=i_utc+1;
          i=0;
          while(Recdata_GPGGA[i_temp+1]!=',')
          {
            i_temp++;
          }
          uint i_weidu=i_utc+1;
          i=0;
          while(Recdata_GPGGA[i_weidu+1]!=',')
          {
            WeiDu[i++]=Recdata_GPGGA[i_weidu+1];
            i_weidu++;
          }
          uint i_N=i_weidu+1;
          i=0;
          while(Recdata_GPGGA[i_N+1]!=',')
          {
            i_N++;
          }
          uint i_JingDu=i_N+1;
          i=0;
          while(Recdata_GPGGA[i_JingDu+1]!=',')
          {
            JingDu[i++]=Recdata_GPGGA[i_JingDu+1];
            i_JingDu++;
          }
          /*for (uint i=0;i<6;i++)
            UTC[i]=Recdata_GPRMC[i+1];
          if (Recdata_GPRMC[14]==',')
          {
            for (uint i=0;i<9;i++)
              WeiDu[i]='*';
            for (uint i=0;i<10;i++)
              JingDu[i]='*';
          }
          else
          {
            for (uint i=0;i<9;i++)
              WeiDu[i]=Recdata_GPRMC[i+14];
            for (uint i=0;i<10;i++)
              JingDu[i]=Recdata_GPRMC[i+26];
          }*/
      }
      else if(Recdata[check]=='$'&&Recdata[check+1]=='G'&&Recdata[check+2]=='P'
           &&Recdata[check+3]=='V'&&Recdata[check+4]=='T'&&Recdata[check+5]=='G')
      {
          position=check+6;
          while(Recdata_GPVTG_start<70&&Recdata[position++]!='$')
          {
            Recdata_GPVTG[Recdata_GPVTG_start++]=Recdata[position-1];
            Recdata_GPGSV_start=0;
            Recdata_GPGSA_start=0;
            Recdata_GPRMC_start=0;
            Recdata_GPGGA_start=0;
          }
      }
      check=check+1;
  } 	  
}
void GpsMeasurement_Init( uint8 task_id )
{
  Gps_Uart_TaskID = task_id;
  Gps_Uart_TransID = 0;

#if defined ( LCD_SUPPORTED )
  //HalLcdWriteString( "TempHumApp", HAL_LCD_LINE_1 );
#endif
  
  osal_start_timerEx( Gps_Uart_TaskID,
                        GPSMEASUREMENT_EVT,
                        GPSMEASUREMENT_TIMEOUT );
  
  RegisterForKeys( Gps_Uart_TaskID );
}
uint16 GpsMeasurement_ProcessEvent( uint8 task_id, uint16 events )
{
  if ( events & GPSMEASUREMENT_EVT )
  {

  HalUARTRead(0, Recdata, 300);
  handle();
  osal_start_timerEx( Gps_Uart_TaskID,
                        GPSMEASUREMENT_EVT,
                        GPSMEASUREMENT_TIMEOUT );


#if defined ( LCD_SUPPORTED )
  //HalLcdWriteStringValue("Temp: ", Temp, 10, HAL_LCD_LINE_3);
  //HalLcdWriteStringValue("Hum:  ", Hum,  10, HAL_LCD_LINE_4);
#endif

    // return unprocessed events
    return (events ^ GPSMEASUREMENT_EVT);
  }

  // Discard unknown events
  return 0;
}
