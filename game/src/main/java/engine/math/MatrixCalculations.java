package engine.math;

import engine.math.numericalObjects.Matrix4;

public class MatrixCalculations {

    public static Matrix4 invert(Matrix4 m) {
        double determinant = determinant(m);
        if (determinant == 0) { return new Matrix4(); }

        Matrix4 a = adjugate(m);

        // inverse = adjugate matrix / determinant
        return a.times(1/determinant);
    }

    private static Matrix4 adjugate(Matrix4 m) {
        Matrix4 n = new Matrix4();

        n.setA11(m.getA23()*m.getA34()*m.getA42() - m.getA24()*m.getA33()*m.getA42() + m.getA24()*m.getA32()*m.getA43() - m.getA22()*m.getA34()*m.getA43() - m.getA23()*m.getA32()*m.getA44() + m.getA22()*m.getA33()*m.getA44());
        n.setA12(m.getA14()*m.getA33()*m.getA42() - m.getA13()*m.getA34()*m.getA42() - m.getA14()*m.getA32()*m.getA43() + m.getA12()*m.getA34()*m.getA43() + m.getA13()*m.getA32()*m.getA44() - m.getA12()*m.getA33()*m.getA44());
        n.setA13(m.getA13()*m.getA24()*m.getA42() - m.getA14()*m.getA23()*m.getA42() + m.getA14()*m.getA22()*m.getA43() - m.getA12()*m.getA24()*m.getA43() - m.getA13()*m.getA22()*m.getA44() + m.getA12()*m.getA23()*m.getA44());
        n.setA14(m.getA14()*m.getA23()*m.getA32() - m.getA13()*m.getA24()*m.getA32() - m.getA14()*m.getA22()*m.getA33() + m.getA12()*m.getA24()*m.getA33() + m.getA13()*m.getA22()*m.getA34() - m.getA12()*m.getA23()*m.getA34());
        n.setA21(m.getA24()*m.getA33()*m.getA41() - m.getA23()*m.getA34()*m.getA41() - m.getA24()*m.getA31()*m.getA43() + m.getA21()*m.getA34()*m.getA43() + m.getA23()*m.getA31()*m.getA44() - m.getA21()*m.getA33()*m.getA44());
        n.setA22(m.getA13()*m.getA34()*m.getA41() - m.getA14()*m.getA33()*m.getA41() + m.getA14()*m.getA31()*m.getA43() - m.getA11()*m.getA34()*m.getA43() - m.getA13()*m.getA31()*m.getA44() + m.getA11()*m.getA33()*m.getA44());
        n.setA23(m.getA14()*m.getA23()*m.getA41() - m.getA13()*m.getA24()*m.getA41() - m.getA14()*m.getA21()*m.getA43() + m.getA11()*m.getA24()*m.getA43() + m.getA13()*m.getA21()*m.getA44() - m.getA11()*m.getA23()*m.getA44());
        n.setA24(m.getA13()*m.getA24()*m.getA31() - m.getA14()*m.getA23()*m.getA31() + m.getA14()*m.getA21()*m.getA33() - m.getA11()*m.getA24()*m.getA33() - m.getA13()*m.getA21()*m.getA34() + m.getA11()*m.getA23()*m.getA34());
        n.setA31(m.getA22()*m.getA34()*m.getA41() - m.getA24()*m.getA32()*m.getA41() + m.getA24()*m.getA31()*m.getA42() - m.getA21()*m.getA34()*m.getA42() - m.getA22()*m.getA31()*m.getA44() + m.getA21()*m.getA32()*m.getA44());
        n.setA32(m.getA14()*m.getA32()*m.getA41() - m.getA12()*m.getA34()*m.getA41() - m.getA14()*m.getA31()*m.getA42() + m.getA11()*m.getA34()*m.getA42() + m.getA12()*m.getA31()*m.getA44() - m.getA11()*m.getA32()*m.getA44());
        n.setA33(m.getA12()*m.getA24()*m.getA41() - m.getA14()*m.getA22()*m.getA41() + m.getA14()*m.getA21()*m.getA42() - m.getA11()*m.getA24()*m.getA42() - m.getA12()*m.getA21()*m.getA44() + m.getA11()*m.getA22()*m.getA44());
        n.setA34(m.getA14()*m.getA22()*m.getA31() - m.getA12()*m.getA24()*m.getA31() - m.getA14()*m.getA21()*m.getA32() + m.getA11()*m.getA24()*m.getA32() + m.getA12()*m.getA21()*m.getA34() - m.getA11()*m.getA22()*m.getA34());
        n.setA41(m.getA23()*m.getA32()*m.getA41() - m.getA22()*m.getA33()*m.getA41() - m.getA23()*m.getA31()*m.getA42() + m.getA21()*m.getA33()*m.getA42() + m.getA22()*m.getA31()*m.getA43() - m.getA21()*m.getA32()*m.getA43());
        n.setA42(m.getA12()*m.getA33()*m.getA41() - m.getA13()*m.getA32()*m.getA41() + m.getA13()*m.getA31()*m.getA42() - m.getA11()*m.getA33()*m.getA42() - m.getA12()*m.getA31()*m.getA43() + m.getA11()*m.getA32()*m.getA43());
        n.setA43(m.getA13()*m.getA22()*m.getA41() - m.getA12()*m.getA23()*m.getA41() - m.getA13()*m.getA21()*m.getA42() + m.getA11()*m.getA23()*m.getA42() + m.getA12()*m.getA21()*m.getA43() - m.getA11()*m.getA22()*m.getA43());
        n.setA44(m.getA12()*m.getA23()*m.getA31() - m.getA13()*m.getA22()*m.getA31() + m.getA13()*m.getA21()*m.getA32() - m.getA11()*m.getA23()*m.getA32() - m.getA12()*m.getA21()*m.getA33() + m.getA11()*m.getA22()*m.getA33());

        return n;
    }

    private static double determinant(Matrix4 m) {
        return  m.getA14()*m.getA23()*m.getA32()*m.getA41() - m.getA13()*m.getA24()*m.getA32()*m.getA41() - m.getA14()*m.getA22()*m.getA33()*m.getA41() + m.getA12()*m.getA24()*m.getA33()*m.getA41()+
                m.getA13()*m.getA22()*m.getA34()*m.getA41() - m.getA12()*m.getA23()*m.getA34()*m.getA41() - m.getA14()*m.getA23()*m.getA31()*m.getA42() + m.getA13()*m.getA24()*m.getA31()*m.getA42()+
                m.getA14()*m.getA21()*m.getA33()*m.getA42() - m.getA11()*m.getA24()*m.getA33()*m.getA42() - m.getA13()*m.getA21()*m.getA34()*m.getA42() + m.getA11()*m.getA23()*m.getA34()*m.getA42()+
                m.getA14()*m.getA22()*m.getA31()*m.getA43() - m.getA12()*m.getA24()*m.getA31()*m.getA43() - m.getA14()*m.getA21()*m.getA32()*m.getA43() + m.getA11()*m.getA24()*m.getA32()*m.getA43()+
                m.getA12()*m.getA21()*m.getA34()*m.getA43() - m.getA11()*m.getA22()*m.getA34()*m.getA43() - m.getA13()*m.getA22()*m.getA31()*m.getA44() + m.getA12()*m.getA23()*m.getA31()*m.getA44()+
                m.getA13()*m.getA21()*m.getA32()*m.getA44() - m.getA11()*m.getA23()*m.getA32()*m.getA44() - m.getA12()*m.getA21()*m.getA33()*m.getA44() + m.getA11()*m.getA22()*m.getA33()*m.getA44();
    }

}
