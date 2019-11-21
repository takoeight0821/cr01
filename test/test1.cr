fix f x = f (fix f) x;
fact f x =
  if x == 0
  then 1
  else x * f (x - 1);
main _ = (fix fact) 5;