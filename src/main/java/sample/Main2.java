package sample;

public class Main2 {

  public static void main(String[] args) {

    System.out.println(isPalindrom("kajak"));
    System.out.println(isPalindrom("dupaaaaaaa"));
    System.out.println(isPalindrom("ka jak"));
    System.out.println(isPalindrom("Kajak."));
  }

  private static boolean isPalindrom(String string) {
    if (string != null) {
      return new StringBuilder(string)
          .reverse()
          .toString()
          .replaceAll("[^a-zA-Z]", "")
          .toUpperCase()
          .equals(string.toUpperCase().replaceAll("[^A-Z]", ""));
    }
    return false;
  }
}
