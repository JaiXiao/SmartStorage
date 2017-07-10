#ifndef __DHT11_H__
#define __DHT11_H__

unsigned char Temperature,Humidity;
extern void Delay_ms(unsigned int xms);	//延时函数
extern void DHT11(void);   //温湿传感启动

#endif