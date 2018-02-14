package com.swed.pos.model;

public class ConsumerRecord
{
  private String cardNum;
  private String money;
  private String status;
  private String time;
  
  public String getCardNum()
  {
    return this.cardNum;
  }
  
  public String getMoney()
  {
    return this.money;
  }
  
  public String getStatus()
  {
    return this.status;
  }
  
  public String getTime()
  {
    return this.time;
  }
  
  public void setCardNum(String paramString)
  {
    this.cardNum = paramString;
  }
  
  public void setMoney(String paramString)
  {
    this.money = paramString;
  }
  
  public void setStatus(String paramString)
  {
    this.status = paramString;
  }
  
  public void setTime(String paramString)
  {
    this.time = paramString;
  }
}


