fact x =
  if x == 0
  then 1
  else x * fact (x - 1);
main _ = fact 5;