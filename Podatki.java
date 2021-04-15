import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Podatki {
    public static Scanner scan = new Scanner(System.in);
    public static double totalNetRevenue;
    public static double totalNetCost;
    public static double totalIncomeTax;
    public static double totalVAT;
    public static double VATtoDeduct;
    public static double VATtoPay;
    public static double taxBase;
    public static double taxDeduction;
    public static String formaOpodatkowania;
    public static final double taxTreshold = 8500;

    public static void addNetRevenue(double newRevenueNet) {
        totalNetRevenue += newRevenueNet;
        taxBase += newRevenueNet;
        totalVAT += newRevenueNet * 0.23;
        System.out.println("Dodano kwotę " + newRevenueNet + " do przychodów.\nTwoje Przychody wynoszą aktualnie: " + totalNetRevenue);
        System.out.println("=====================================================================");
    }

    public static void checkTaxes() {
        VATtoPay = totalVAT - VATtoDeduct;
        System.out.printf("Twój przychód wynosi: %.2f\n", getTotalNetRevenue());
        System.out.printf("Twój koszty wynoszą: %.2f\n", getTotalNetCost());
        System.out.printf("Twój podatek dochodowy wynosi: %.2f\n", getTotalIncomeTax());
        System.out.printf("Twój podatek VAT wynosi: %.2f\n", getVATtoPay());
        System.out.println("=====================================================================");
    }

    public static void addNewCost(double newCost, boolean isCarExpense) {
        double newCostNet = newCost / 1.23;
        totalNetCost += newCostNet;
        double newCostVat = newCost * 0.23 / 1.23;
        if (!isCarExpense) {
            taxBase -= newCostNet;
            VATtoDeduct += newCostVat;
        } else {
            taxBase -= (newCostNet + newCostVat * 0.5) * 0.75;
            VATtoDeduct += newCostVat * 0.5;
        }
        System.out.println("Dodano nowy koszt " + newCostNet + ".\nSuma twoich kosztów wynosi: " + totalNetCost);
        System.out.println("=====================================================================");
    }

    public static void addZUS(double healthTax, double socialTax){
        taxDeduction += healthTax * 7.75/9;
        taxBase -= socialTax;
    }

    public static void linearTax(){
        totalIncomeTax = taxBase * 0.19 - taxDeduction;
    }

    public static void progressiveTax(){
        if(taxBase < taxTreshold){
            totalIncomeTax = taxBase * 0.17 - taxDeduction;
        }
        else{
            totalIncomeTax = taxTreshold * 0.17 + (taxBase - taxTreshold) * 0.32 - taxDeduction;
        }
    }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(out);

        System.out.println("Witaj w programie księgowym obliczającym podatki!");
        System.out.println("W pierwszej kolejności wybierz formę opodatkowania:");
        Pattern pattern1 = Pattern.compile("[12]");
        Pattern pattern2 = Pattern.compile("[123456]");
        Pattern pattern3 = Pattern.compile("[0-9]{0,9}\\.?[0-9]{0,2}");
        Pattern pattern4 = Pattern.compile("tak|nie");
        do {
            System.out.println("1) jeśli rozliczasz się progresywnie");
            System.out.println("2) jeśli rozliczasz się liniowo");
            formaOpodatkowania = getUserInput();
            if (!pattern1.matcher(formaOpodatkowania).matches()) {
                System.out.println("Nie wybrano 1 ani 2. Wybierz ponownie");
            }
        }
        while (!pattern1.matcher(formaOpodatkowania).matches());
        System.out.println("Co chcesz teraz zrobić?");
        String chooseOption;
        do {
            System.out.println("1) Sprawdź podatki");
            System.out.println("2) Dodaj przychód");
            System.out.println("3) Dodaj koszt");
            System.out.println("4) Dodaj ZUS");
            System.out.println("5) Resetuj koszty");
            System.out.println("6) Zmień formę opodatkowania");
            System.out.println("7) Zakończ");
            chooseOption = getUserInput();
            if (!pattern2.matcher(formaOpodatkowania).matches()) {
                System.out.println("Nie wybrano liczby z przedziału 1-5. Wybierz ponownie");
            }
            else if (chooseOption.equals("1")) {
                if (formaOpodatkowania.equals("1")) {
                    progressiveTax();
                    checkTaxes();
                } else if (formaOpodatkowania.equals("2")) {
                    linearTax();
                    checkTaxes();
                }
            }
            else if (chooseOption.equals("2")) {
                System.out.println("Podaj kwotę jaką chcesz dodać do przychodów: ");
                String revenue;
                do{
                    revenue = getUserInput();
                    if(!pattern3.matcher(revenue).matches()){
                        System.out.println("podaj przychód wyrażony liczbą");
                    }
                }
                while(!pattern3.matcher(revenue).matches());
                double revenueToAdd = Double.parseDouble(revenue);
                addNetRevenue(revenueToAdd);
            }
            else if (chooseOption.equals("3")) {
                System.out.println("Dodaj koszt: ");
                String cost;
                do{
                    cost = getUserInput();
                    if(!pattern3.matcher(cost).matches()){
                        System.out.println("podaj koszt wyrażony liczbą");
                    }
                }
                while(!pattern3.matcher(cost).matches());
                double costToAdd = Double.parseDouble(cost);
                System.out.println("Czy koszt jest związany z samochodem: ");

                String carExpense;
                do{
                    carExpense = getUserInput();
                    if(!pattern4.matcher(carExpense).matches()){
                        System.out.println("podaj koszt wyrażony liczbą");
                    }
                }
                while(!pattern4.matcher(carExpense).matches());
                boolean isCarExpense = true;
                if(carExpense.equals("tak")) isCarExpense = true;
                else if(carExpense.equals("nie")) isCarExpense = false;
                addNewCost(costToAdd, isCarExpense);
            }
            else if (chooseOption.equals("4")) {
                //metoda dodaj ZUS
                String health;
                do{
                    System.out.println("Podaj wysokość składki zdrowotnej:");
                    health = getUserInput();
                    if(!pattern3.matcher(health).matches()){
                        System.out.println("podaj wysokośc składek w liczbie");
                    }
                }
                while(!pattern3.matcher(health).matches());
                double healthTax = Double.parseDouble(health);
                String social;
                do{
                    System.out.println("Podaj wysokość składek społecznych:");
                    social = getUserInput();
                    if(!pattern3.matcher(social).matches()){
                        System.out.println("podaj wysokość składek w liczbie");
                    }
                }
                while(!pattern3.matcher(social).matches());
                double socialTax = Double.parseDouble(social);
                addZUS(healthTax, socialTax);
            }
            else if (chooseOption.equals("5")) {
                resetAll();
            }
            else if (chooseOption.equals("6")) {
                do {
                    System.out.println("1) jeśli rozliczasz się progresywnie");
                    System.out.println("2) jeśli rozliczasz się liniowo");
                    formaOpodatkowania = getUserInput();
                    if (!pattern1.matcher(formaOpodatkowania).matches()) {
                        System.out.println("Nie wybrano 1 ani 2. Wybierz ponownie");
                    }
                }
                while (!pattern1.matcher(formaOpodatkowania).matches());
            }
        }
        while (!chooseOption.equals("7"));


    }

    public static String getUserInput() {
        return scan.nextLine();
    }

    public static void resetAll(){
        totalNetRevenue = 0;
        totalNetCost = 0;
        totalIncomeTax = 0;
        totalVAT = 0;
        VATtoDeduct = 0;
        VATtoPay = 0;
        taxBase = 0;
        taxDeduction = 0;
    }

    public static double getTotalIncomeTax() {
        return totalIncomeTax;
    }
    public static double getTotalNetRevenue() {
        return totalNetRevenue;
    }
    public static double getTotalNetCost() {
        return totalNetCost;
    }
    public static double getVATtoPay() {
        return VATtoPay;
    }
}

