package cl.maraneda.previred.util;

import cl.maraneda.previred.exceptions.RUTException;
import cl.maraneda.previred.exceptions.RutRuntimeException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Esta clase es una implementación del RUT usado en Chile
 *
 * @author Tomas Barros <a href="mailto:bbarros@nic.cl">bbarros@nic.cl</a>
 * @version 1.0
 *
 */
public class RUT {

    private final int[] numero;
    private char digito;
    public final short RUT_LENGTH = 9;

    /**
     * Crea un nuevo RUT vacío
     */
    public RUT() {
        numero = new int[RUT_LENGTH];
    }

    /**
     * Genera un rut a partir de un string en formato xxxxxxxx-x
     * o formato xx.xxx.xxx-x
     *
     * @param rut El String representativo del rut
     * @throws RUTException En caso que el String del RUT no esté bien formado
     * o el digito verificador no corresponda
     */
    public RUT(String rut) throws RUTException {
        this();
        String rutcompleto=rut;
        char dig = rut.charAt(rut.length() - 2);
        if (dig != '-')
            throw (new RUTException("El RUT no esta bien formado (Sin guion)"));
        dig = rut.charAt(rut.length() - 1);

        if (!Character.isDigit(dig) && Character.toUpperCase(dig) != 'K')
            throw (new RUTException("El RUT no está bien formado (" + dig + " no es un digito verificador valido)"));

        digito = Character.toUpperCase(dig);

        rut = rut.substring(0, rut.length() - 2);

        int sl = rut.length();

        if (sl > RUT_LENGTH + (RUT_LENGTH / 3))
            throw (
                    new RUTException("La cantidad de dígitos del RUT \"" + rutcompleto + "\"no es valido"));

        AtomicInteger j = new AtomicInteger(0);
        StringBuilder sbrut = new StringBuilder(rut);
        IntStream.range(0, sl).forEach(i -> {
            char ch = sbrut.toString().charAt(sl - i - 1);
            if (j.get() != 0 && (j.get() % 3) == 0 && ch == '.')
                return;
            if (!Character.isDigit(ch))
                throw (new RutRuntimeException("El RUT no está bien formado (el caracter " + ch + " no es un digito)"));
            numero[RUT_LENGTH - 1 - j.getAndIncrement()] =
                    Character.digit(ch, Character.LETTER_NUMBER);
        });
        if (Character.toUpperCase(digito) != getDigito())
            throw (new RUTException("El dígito verificador del rut " + rut + " no corresponde:"+digito+" <> "+getDigito()));
    }

    /**
     * Obtiene el RUT en formato xx.xxx.xxx-x
     *
     * @return El String del RUT
     */
    public String getFormated() {
        StringBuilder value=new StringBuilder();
        boolean[] escribir = {false};
        AtomicInteger point = new AtomicInteger();
        Arrays.stream(numero).forEach(num -> {
            if(num != 0){
                escribir[0] = true;
            }
            if(point.get() !=0 && point.get() % 3 == 0){
                value.append(".");
            }
            if(escribir[0]){
                value.append(
                    Character.toUpperCase(
                        Character.forDigit(num, Character.LETTER_NUMBER)));
            }
            point.incrementAndGet();
        });

        return value.append("-").append(digito).toString();
    }

    /**
     * Obtiene el RUT en formato xxxxxxxx-x
     *
     * @return El String del RUT
     */
    public String toString() {
        return (getPure() + "-" + digito);
    }

    /**
     * Obtiene el RUT del contribuyente sin puntos, guiones ni dígito verificador
     *
     * @return el RUT del contribuyente sin puntos, guiones ni dígito verificador
     */
    public String getPure() {
        StringBuilder value = new StringBuilder();
        boolean[] escribir = {false};
        IntStream.range(0, RUT_LENGTH).forEach(i -> {
            if (numero[i] != 0)
                escribir[0] = true;
            if (escribir[0])
                value.append(Character.toUpperCase(
                        Character.forDigit(numero[i], Character.LETTER_NUMBER)));
        });
        return (value.toString());
    }

    public static char getDigito(int num){
        String snum=String.valueOf(num);
        int[] anum = new int[snum.length()];
        AtomicInteger j = new AtomicInteger();
        AtomicInteger mult = new AtomicInteger(2);
        AtomicInteger suma = new AtomicInteger();
        snum.chars().forEach(c ->
            anum[j.getAndIncrement()]=Character.digit(c, Character.LETTER_NUMBER)
        );

        Arrays.stream(anum).boxed().toList().reversed().forEach(n ->{
             suma.addAndGet(mult.get() * n);
             if(mult.get() == 7){
                 mult.set(2);
             }else{
                 mult.incrementAndGet();
             }
        });

        int mod = suma.get() % 11;

        return switch (mod) {
            case 0 -> ('0');
            case 1 -> ('K');
            default -> (
                    Character.toUpperCase(
                            Character.forDigit(
                                    11 - mod,
                                    Character.LETTER_NUMBER)));
        };
    }
    /**
     * Calcula el dígito verificador del RUT
     *
     * @return El dígito verificador
     */
    public char getDigito() {
        return RUT.getDigito(Integer.parseInt(this.getPure()));
    }

    /**
     * Calcula el dígito verificador del RUT
     *
     * @return El dígito verificador
     */
    public String getSDigito() {
        return Character.toString(this.getDigito());
    }

    /**
     * Compara que dos RUTs son iguales
     * @param obj El RUT contra el cual se desea comprar
     * @return true en caso que sean iguales o false en caso contrario
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof RUT rut))
            return (false);
        if (rut.digito != digito)
            return (false);
        return Arrays.equals(rut.numero, this.numero);
    }
}

