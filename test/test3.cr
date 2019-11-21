s x = fn y -> fn z -> x z (y z);
k x = fn y -> x;
main _ =
  let i = s k k
  in (i i) (2 + 10 * 4);