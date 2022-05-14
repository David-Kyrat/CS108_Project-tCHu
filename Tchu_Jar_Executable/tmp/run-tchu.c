#include <stdlib.h>
int main(void) {
  system("set pt=\%CD\%");
  system("cd files && run-tchu.bat");
 // system("run-tchu.bat");
  system("cd \%pt\%");
  return 0;
}
