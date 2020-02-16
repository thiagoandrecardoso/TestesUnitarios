package br.ce.wcaquino.servicos;

import org.junit.*;

//@RunWith(ParallelRunner.class)
public class CalculadoraTest {

    public static StringBuffer ordem = new StringBuffer();

    Calculadora calculadora;

    @Before
    public void setUp() {
        calculadora = new Calculadora();
        System.out.println("iniciando");
    }

    @Test
    public void deveSomarDoisNumeros() {
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros1() {
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros2() {
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros3() {
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }


    @After
    public void tearDown() {
        System.out.println("finalizando");
        ordem.append("1");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println(ordem);
    }
}
