package org.erikd.junit5app.ejemplos.models;

import org.erikd.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    /**Agregamos estos atributos para que podamos acceder a ellos desde cada test.*/
    private TestInfo testInfo;
    private TestReporter testReporter;


    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Pepe Biondi", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Starting method.");
        System.out.println("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() +
                " | con las etiquetas " + testInfo.getTags() +" |");

        //Ahora el testReporter
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() +
                " | con las etiquetas " + testInfo.getTags() +" |");//esto utilizara la salida del log de junit
    }

    @AfterEach
    void tearDown() {
        System.out.println("Ending testing method.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Staring test class.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Ending test class.");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Testing account attributes")
    class AccountNameBalance{

        /**Probando TestInfo y TestReporter**/
        @Test
        @DisplayName("Testing Account Name.")
        void testNombreCuenta() {
            //System.out.println(testInfo.getTags());
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")){
                //System.out.println("Do something with cuenta tag");
                testReporter.publishEntry("Do something with cuenta tag");//lo mismo que lo anterior pero publicando en el log de junit
            }

            //Cuenta cuenta = new Cuenta("Pepe Biondi", new BigDecimal("1000.12345"));
            //cuenta.setPersona("Pepe Albistur");
            String esperado = "Pepe Biondi";
            String persona = cuenta.getPersona();
            //The lambda expressions produces the Strings only when the assert fails. Saving resources.
            assertNotNull(persona, () -> "La persona no puede ser nula en la cuenta.");
            assertEquals(esperado, cuenta.getPersona(), () -> "El nombre de la cuenta no es el que se esperaba.");
            assertTrue(cuenta.getPersona().equals(esperado), () -> "Persona de la cuenta no es la esperada.");

        }

        @Test
        @DisplayName("Testing Account Balance. Not Null. Greater than zero.")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);//chequeando que la cuenta tenga saldo mayor a cero
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//validando lo mismo al revés. chequendo que la cuenta tenga saldo mayor a cero
        }

        @Test
        @DisplayName("Testing references")
        void testReferenciaCuenta() {//testeando por instancia
            cuenta = new Cuenta("John Doe", new BigDecimal("8900.9998"));//real
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9998"));//esperado
            assertNotNull(cuenta.getSaldo());
//        assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);//falla porque son dos instancias distintas
        }
    }


    @Nested
    class AccountOperations{
        /***
         *  Aqui vemos primero el test y después el desarrollo necesario en el metodo
         *  para cumplir con el resultado exigido por el método. Esto es Test Driven Development.TDD.
         *  Falta la implementacion de los metodos.
         */

        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @DisplayName("Test Debito Cuenta Repetir")
        @RepeatedTest(value = 5, name = "{displayName} - Repetition number {currentRepetition} de {totalRepetitions}")
        void testDebitoCuentaRepetir(RepetitionInfo info) {
            if(info.getCurrentRepetition()==3){
                System.out.println("We are on repetition number=" + info.getCurrentRepetition());
            }
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("error")
        @Tag("cuenta")
        @Test
        void testDineroInsuficienteExceptionCuenta() {
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal(1500));
            });
            String actual = exception.getMessage();
            String esperado = "Dinero insuficiente";
            assertEquals(esperado, actual);
        }


        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }

    }

    @Tag("banco")
    @Tag("cuenta")
    @Test
    @DisplayName("Bank Account Relationship test. Using lambdas.")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
        assertEquals("Banco del Estado", cuenta2.getBanco().getNombre());


        //three ways to assert the relation from the bank to the accounts
        assertEquals("Andres", banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Andres"))
                .findFirst()
                .get().getPersona());

        assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Andres"))
                .findFirst().isPresent());

        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("John Doe")));


    }

    /**
     * AssertAll using lambda expressions
     */
    @Test
    //@Disabled
    void testRelacionBancoCuentasAssertAll() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        //as always we can delete the braces if the assert block contains only one line
        assertAll(
                () -> {
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo no es el esperado.");
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString(), () -> "El valor del saldo no es el esperado.");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta2.getBanco().getNombre());
                },

                //three forms to assert the relation from the bank to the accounts
                () -> {
                    assertEquals("Andres", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst()
                            .get().getPersona());
                },

                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst().isPresent());
                },

                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("John Doe")));
                }

        );
        /**Junit 5 works with Lambda Expressions*/
    }

    @Nested
    class OperatingSystem {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }

    }

    @Nested
    class JavaVersion {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJDK11() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void testNoJDK15() {

        }

    }

    @Nested
    class SystemProperties {

        @Test
        void printSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.*.*")
        void testJavaVersion() {

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {

        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNO64() {

        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "Erik")
        void testUserName() {

        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {

        }

    }

    @Nested
    class EnviromentVariable {

        @Test
        void printEnviromentVariables() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.*")//Wildcards are used to avoid full path
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcessor() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "env")
        void testEnvironment() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvironmentProductionDisabled() {

        }

        //Assumptions
        @Test
        @DisplayName("Testing Account Balance. Not Null. Greater than zero. ONLY ON DEV ENV.")
        void testSaldoCuentaDevEnv() {
            boolean isDevEnv = "dev".equals(System.getProperty("ENV"));
            assumeTrue(isDevEnv);

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Testing Account Balance. Not Null. Greater than zero. ONLY ON DEV ENV. Using AssumingThat")
        void testSaldoCuentaDevEnv2() {
            boolean isDevEnv = "dev".equals(System.getProperty("ENV"));
            assumingThat(isDevEnv, () -> {//if the condition is not met this block is no executed but the rest of the blocks outside this one will be executed
                assertNotNull(cuenta.getSaldo());
                //assertEquals(1000.123456,cuenta.getSaldo().doubleValue());
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);

            });
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    /**Test con variables parametrizadas con method source. NO VA DENTRO DE LA CLASE DE PARAMETRIZADAS
     * PORQUE REQUIERE DEL USO DEL METODO montoList estático*/
    @Tag("param")
    @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {//inyecta el valor parametrizado arriba
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
    }

    static List<String> montoList(){
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }


    @Tag("param")
    @Nested
    class ParameterizedTests{

        /**Test con variables parametrizadas con value source*/
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        void testDebitoCuentaValueSource(String monto) {//inyecta el valor parametrizado arriba
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
        }

        /**Test con variables parametrizadas con value csv*/
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {//inyecta el valor parametrizado arriba
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
        }

        /**Test con variables parametrizadas con file csv*/
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {//inyecta el valor parametrizado arriba
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
        }

        /**Test con variables parametrizadas con value csv*. Prueba teniendo como valores saldo y monto*/
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}") //dos formas de pasar el valor con {0} y {argumentsWithNames}
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {//inyecta el valor parametrizado arriba
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
        }

        /**Test con variables parametrizadas con file csv*/
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {//inyecta el valor parametrizado arriba
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);


            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//probamos que sea mayor a cero
        }
    }

    @Nested
    @Tag("timeout")
    class TimeOutTest{
        @Test
        @Timeout(1)//5 seconds timeout (seconds default)
        void timeOutTest() throws InterruptedException{
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)//5 seconds timeout
        void timeOutTest2() throws InterruptedException{
            TimeUnit.MILLISECONDS.sleep(900);
        }

        //otra forma de hacer lo mismo
        @Test
        void testTimeOutAssertions(){
            assertTimeout(Duration.ofSeconds(5), () ->{
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }


}
