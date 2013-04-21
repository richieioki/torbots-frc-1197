#pragma once

#include "WPILib.h"

//The purpose of this class is to hide exactly how many Jaguar drives we use per wheel
//In future years if we end up with only two or as many six motors we can just limit that
//change to this class. We could make this class more sophisticated by using vector<Jaguar>
//and dynamically allocating but that might be a future enhancement. For now the class
//knows how many jaguars per side.
class TorJagDrive
{
public:
  TorJagDrive(Jaguar& left1, Jaguar& left2, Jaguar& right1, Jaguar& right2);
  TorJagDrive(Jaguar& left, Jaguar& right);
  void SetDrive(float leftSpeed, float rightSpeed);
  //The following two classes might be private if we always use the above Drive()
  void SetLeft(float speed);
  void SetRight(float speed);
 
  
private:
  Jaguar& m_left1;
  Jaguar& m_left2;
  Jaguar& m_right1;
  Jaguar& m_right2;
  int numOfJags;
};
