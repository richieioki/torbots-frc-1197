#include "TorJagDrive.h"

TorJagDrive::TorJagDrive(Jaguar& left1,Jaguar& left2, Jaguar& right1, Jaguar& right2 )
: m_left1 (left1), m_left2(left2), m_right1(right1), m_right2(right2)
{}


void TorJagDrive::SetDrive(float leftSpeed, float rightSpeed)
{
  SetLeft(leftSpeed);
  SetRight(rightSpeed);
}

void TorJagDrive::SetLeft(float speed)
{
  // left motors take positive speed to move forward
  m_left1.Set(-speed);
  m_left2.Set(-speed);
}

void TorJagDrive::SetRight(float speed)
{
  // right motors are inverted. Change sign for driving right side
  m_right1.Set(speed);
  m_right2.Set(speed);
}
