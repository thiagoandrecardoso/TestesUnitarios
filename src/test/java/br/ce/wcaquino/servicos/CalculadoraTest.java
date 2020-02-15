package br.ce.wcaquino.servicos;

import br.ce.wcaquino.runners.ParallelRunner;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {
    Calculadora calculadora;

    @Before
    public void setUp(){
        calculadora = new Calculadora();
        System.out.println("iniciando");
    }

    @Test
    public void deveSomarDoisNumeros(){
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros1(){
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros2(){
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }

    @Test
    public void deveSomarDoisNumeros3(){
        Assert.assertEquals(5, calculadora.somar(2, 3));
    }


    @After
    public void tearDown(){
        System.out.println("finalizando");
    }
}
