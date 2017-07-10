/*******************************************************
文件名:DHT11.c
版本号:v1.0
创建日期:2013-10-12
作者：楼燚航
硬件描述：CC2530 P1_3连接DHT11 data引脚
主要函数描述：DHT11()函数将DHT11的温湿度数据读出，保存在Temperature和Humidity中
********************************************************/
#include <ioCC2530.h>

#define uint unsigned int
#define U8 unsigned char


#define DATA_PIN P1_3


U8 U8FLAG,U8temp;//温湿度定义
U8 Humidity_H,Humidity_L;//定义湿度存放变量
U8 Temperature,Humidity;//定义温度存放变量
U8 U8T_data_H,U8T_data_L,U8RH_data_H,U8RH_data_L,U8checkdata;
U8 U8T_data_H_temp,U8T_data_L_temp,U8RH_data_H_temp,U8RH_data_L_temp,U8checkdata_temp;
U8 U8comdata;


void Delay_10us() //10 us延时
{
   U8 i;
   for(i=0;i<16;i++)
   asm("NOP");
}

void Delay_ms(uint Time)//n ms延时
{
  unsigned char i;
  while(Time--)
  {
    for(i=0;i<100;i++)
     Delay_10us();
  }
}


/***********************
   温湿度传感
***********************/
void COM(void)	// 温湿写入
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

//-------------------------------- 　　
//-----湿度读取子程序 ------------ 　　
//-------------------------------- 　　
//----以下变量均为全局变量-------- 　　
//----温度高8位== U8T_data_H------ 　　
//----温度低8位== U8T_data_L------ 　　
//----湿度高8位== U8RH_data_H----- 　　
//----湿度低8位== U8RH_data_L----- 　　
//----校验 8位 == U8checkdata----- 　　
//----调用相关子程序如下---------- 　　
//---- Delay();, Delay_10us();COM(); 　　
//--------------------------------

void DHT11(void)   //温湿传感启动
{
    P1DIR |= (1<<3);
    DATA_PIN=0;
    Delay_ms(19);  //主机拉低18ms
    DATA_PIN=1;     //总线由上拉电阻拉高 主机延时40us
    P1DIR &= ~(1<<3); //重新配置IO口方向
    Delay_10us();
    Delay_10us();						
    Delay_10us();
    Delay_10us();
    //判断从机是否有低电平响应信号 如不响应则跳出，响应则向下运行
     if(!DATA_PIN)
     {
      U8FLAG=2; //判断从机是否发出 80us 的低电平响应信号是否结束
      while((!DATA_PIN)&&U8FLAG++);
      U8FLAG=2;//判断从机是否发出 80us 的高电平，如发出则进入数据接收状态
      while((DATA_PIN)&&U8FLAG++);
      COM();//数据接收状态
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
      //数据校验
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
