/*******************************************************
�ļ���:DHT11.c
�汾��:v1.0
��������:2013-10-12
���ߣ�¥�D��
Ӳ��������CC2530 P1_3����DHT11 data����
��Ҫ����������DHT11()������DHT11����ʪ�����ݶ�����������Temperature��Humidity��
********************************************************/
#include <ioCC2530.h>

#define uint unsigned int
#define U8 unsigned char


#define DATA_PIN P1_3


U8 U8FLAG,U8temp;//��ʪ�ȶ���
U8 Humidity_H,Humidity_L;//����ʪ�ȴ�ű���
U8 Temperature,Humidity;//�����¶ȴ�ű���
U8 U8T_data_H,U8T_data_L,U8RH_data_H,U8RH_data_L,U8checkdata;
U8 U8T_data_H_temp,U8T_data_L_temp,U8RH_data_H_temp,U8RH_data_L_temp,U8checkdata_temp;
U8 U8comdata;


void Delay_10us() //10 us��ʱ
{
   U8 i;
   for(i=0;i<16;i++)
   asm("NOP");
}

void Delay_ms(uint Time)//n ms��ʱ
{
  unsigned char i;
  while(Time--)
  {
    for(i=0;i<100;i++)
     Delay_10us();
  }
}


/***********************
   ��ʪ�ȴ���
***********************/
void COM(void)	// ��ʪд��
{
    U8 i;
    for(i=0;i<8;i++)
    {
     U8FLAG=2;
     /*DATA_PIN=0;
     DATA_PIN=1;*/
     while((!DATA_PIN)&&U8FLAG++);
     Delay_10us();
     Delay_10us();
     Delay_10us();
     U8temp=0;
     if(DATA_PIN)U8temp=1;
     U8FLAG=2;
     while((DATA_PIN)&&U8FLAG++);
     if(U8FLAG==1)break;
     U8comdata<<=1;
     U8comdata|=U8temp;
     }
}

//-------------------------------- ����
//-----ʪ�ȶ�ȡ�ӳ��� ------------ ����
//-------------------------------- ����
//----���±�����Ϊȫ�ֱ���-------- ����
//----�¶ȸ�8λ== U8T_data_H------ ����
//----�¶ȵ�8λ== U8T_data_L------ ����
//----ʪ�ȸ�8λ== U8RH_data_H----- ����
//----ʪ�ȵ�8λ== U8RH_data_L----- ����
//----У�� 8λ == U8checkdata----- ����
//----��������ӳ�������---------- ����
//---- Delay();, Delay_10us();COM(); ����
//--------------------------------

void DHT11(void)   //��ʪ��������
{
    P1DIR |= (1<<3);
    DATA_PIN=0;
    Delay_ms(19);  //��������18ms
    DATA_PIN=1;     //������������������ ������ʱ40us
    P1DIR &= ~(1<<3); //��������IO�ڷ���
    Delay_10us();
    Delay_10us();						
    Delay_10us();
    Delay_10us();
    //�жϴӻ��Ƿ��е͵�ƽ��Ӧ�ź� �粻��Ӧ����������Ӧ����������
     if(!DATA_PIN)
     {
      U8FLAG=2; //�жϴӻ��Ƿ񷢳� 80us �ĵ͵�ƽ��Ӧ�ź��Ƿ����
      while((!DATA_PIN)&&U8FLAG++);
      U8FLAG=2;//�жϴӻ��Ƿ񷢳� 80us �ĸߵ�ƽ���緢����������ݽ���״̬
      while((DATA_PIN)&&U8FLAG++);
      COM();//���ݽ���״̬
      U8RH_data_H_temp=U8comdata;
      COM();
      U8RH_data_L_temp=U8comdata;
      COM();
      U8T_data_H_temp=U8comdata;
      COM();
      U8T_data_L_temp=U8comdata;
      COM();
      U8checkdata_temp=U8comdata;
      DATA_PIN=1;
      //����У��
      U8temp=(U8T_data_H_temp+U8T_data_L_temp+U8RH_data_H_temp+U8RH_data_L_temp);
       if(U8temp==U8checkdata_temp)
      {
          U8RH_data_H=U8RH_data_H_temp;
          U8RH_data_L=U8RH_data_L_temp;
          U8T_data_H=U8T_data_H_temp;
          U8T_data_L=U8T_data_L_temp;
          U8checkdata=U8checkdata_temp;
       }
       Temperature=U8T_data_H;
       Humidity=U8RH_data_H;
    }
    else
    {
      Temperature=0;
      Humidity=0;
    }

}
