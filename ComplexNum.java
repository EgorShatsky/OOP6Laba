public class ComplexNum {
    public double rl;        //действительная часть числа
    public double im;           //мнимая

    public ComplexNum(double rl, double im){
        this.rl = rl;
        this.im = im;
    }

    public double getSquaredModule() {
        return (this.rl * this.rl + this.im * this.im);           //число в квадрате по модулю
    }

    public void makeSquaredInPoint(double x, double y) {
        double real = (rl * rl) - (im * im) + x;                        //считаем комплексное число для метода mandelbrot
        double imagine = 2 * rl * im + y;

        rl = real;
        im = imagine;
    }
    public void makeSquaredWithConjugationInPoint(double x, double y) {
        double real = (rl * rl) - (im * im) + x;                        //так как нужно произвести спряжение ставим -1*(im) мнимая часть числа умножается на -1
        double imagine =  - 2 * rl * im + y;

        rl = real;
        im = imagine;
    }
    public void makeSquaredWithAbsInPoint(double x, double y) {
        double real = (rl * rl) - (im * im) + x;
        double imagine = 2 * Math.abs(rl) * Math.abs(im) + y;           //возводим все в модуль что бы получить абсолютное значение,т.е. число без знака

        rl = real;
        im = imagine;
    }
}
