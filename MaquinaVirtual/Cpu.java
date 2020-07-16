/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desarrollo;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Cpu {

    /***********************************************
    *               Atributos                      * 
    ***********************************************/
    // Punteros
    private int CP, SP, EP;
    // Almacenamiento
    private HashMap<String, Object> registros;
    private Stack<Object> pila;
    private ArrayList<Object> memoria;



    /***********************************************
    *                  Metodos                     * 
    ***********************************************/
    public Cpu() {
        CP=0;   SP=0;  EP=0; // CodePointer - StackPointer - EndPointer
        registros = new HashMap <String, Object>();
        // De la A a la C son registros de uso, RR=Result Reg., LR= Loop Reg.
	registros.put("AR", null);	registros.put("BR", null);	
	registros.put("CR", null);	registros.put("RR", null);
	registros.put("LR", null);      registros.put("JR", null);

        pila = new Stack<Object>();
        memoria = new ArrayList<Object>();
    }

    public String[] getTipo(String palabra)
    {
        String[] tipo = new String[2];
        // Puede ser un booleano
        if(palabra.equals("true") | palabra.equals("false") )
        {                           
            tipo[0]="boolean";
            tipo[1]=palabra;

                           
                            
        }
        // Puede ser un entero
        else if(Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('0'))|Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('1'))|
            Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('2'))|Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('3'))|
            Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('4'))|Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('5'))|
            Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('6'))|Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('7'))|
            Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('8'))|Integer.valueOf(palabra.charAt(0)).equals(Integer.valueOf('9')))
        {
            tipo[0]="int";
            tipo[1]=palabra;
                            
        }
        // Si no, es una cadena
        else
        {
            tipo[0]="String";
            tipo[1]=palabra;
                           
        }
        return tipo;
    }
    
    /*
     *  Convierte una línea de string en un array de instruccion
     */
    public ArrayList<String> transformar(String linea)
    {
        ArrayList<String> instruccion = new ArrayList<String>();
        int tamanio = linea.length();
        if(!linea.isEmpty()){
            int i=0;
            String palabra="";
            
            // Primero buscamos la orden/instruccion 32 = espacio y 44 = coma
            while(tamanio>i & !Integer.valueOf(linea.charAt(i)).equals(32) & !Integer.valueOf(linea.charAt(i)).equals(44))
            {
                palabra+=linea.charAt(i);
                i++;
            }
            // Luego introducimos la instrucción
            instruccion.add(palabra);
            palabra="";
        
            i++;        //Incrementamos para sobrepasar la coma u espacio
            // Después buscamos los operandos
            while(tamanio>i)
            {
                // Si es un elemento de separacion
                if(linea.charAt(i)==' ' | linea.charAt(i)==',')
                {
                    // Si la palabra ya está construida debemos encontrar el tipo de esta
                    if(palabra.length()>0)
                    {
                        // Encontramos el tipo de palabra que es y la añadimos
                        String[] tipo = getTipo(palabra);
                        instruccion.add(tipo[0]);
                        instruccion.add(tipo[1]);
                        palabra="";
                    }
                }
                // Si no es elemento de separacion
                else
                {
                    palabra+=linea.charAt(i);
                }
                
                i++;
            }
            // Si hemos acabado con la palabra comprobamos que no nos falte palabra por añadir
            if(palabra.length()>0)
            {
                String[] tipo = getTipo(palabra);
                instruccion.add(tipo[0]);
                instruccion.add(tipo[1]);
            }
        }
        
        return instruccion;
        
    }
    
    /*
     *  Ejecuta todo el codigo de programa almacenado
     */
    public void ejecutarCodigo()
    {
        while(CP<EP)
        {
            ejecutarInstruccion();
        }
    }
    /*
     *  Ejecuta la siguiente instrucciÃ³n seÃ±alada por el CP y lo incrementa
     */
    public void ejecutarInstruccion()
    {
        ArrayList<String> instruccion = (ArrayList<String>) memoria.get(CP);
        interpretarLinea(instruccion);
        CP++;
        
    }
    
    /*
     *  Carga el archivo indicado en memoria
     */
    public void cargarEnMemoria(File archivo)
    {
        try
        {
            // Cargamos los lectores
            FileReader lector = new FileReader(archivo);
            BufferedReader lector_con_buffer = new BufferedReader(lector);
            
            String linea;
            // Ahora empezamos a desglosar nuestro archivo en instrucciones
            while((linea=lector_con_buffer.readLine())!=null)
            {
                ArrayList<String> instruccion = transformar(linea);
                memoria.add(instruccion);
                EP++;
            }
            
        }catch(Exception e)
        {
            e.printStackTrace();

        }     
    }
    
    /*
     *  Ejecuta la siguiente instrucciÃ³n seÃ±alada por el CP y lo incrementa
     */
    public void interpretarLinea(ArrayList<String> linea)
    {
        // Las lÃ­neas tendrÃ¡n el formato InstrucciÃ³n, Tipo, Operando, Tipo, Operando,...
        String instruccion =(String) linea.get(0);
        switch(instruccion){
            /////////// -> Instruccion MOV
            case "MOV":
                // Si la instrucciÃ³n tiene un primer operando registro
                if(linea.get(1)=="String"){
                    String registro = linea.get(2);
                    // Es registro-registro
                    if(linea.get(3)=="String"){
                        MOV(registro, linea.get(4));
                    }
                    // Es registro-memoria
                    else{
                        MOV(registro, Integer.valueOf(linea.get(4)));
                    }
                }
                // Si no es memoria-registro si o si
                else{
                    int memoria = Integer.valueOf(linea.get(2));
                    MOV(memoria, linea.get(4));
                }
                break;
            /////////// -> Instruccion INS   
            case "INS":
                String registro = linea.get(2);
                // Casteamos el tipo que sea, si no, lo dejamos como string
                if(linea.get(3).equals("String"))   INS(registro, linea.get(4));
                else if (linea.get(3).equals("int")) INS(registro, Integer.valueOf(linea.get(4)));
                else if (linea.get(3).equals("boolean"))    INS(registro, Boolean.parseBoolean(linea.get(4)));
                else INS(registro, linea.get(4));
                break;
            /////////// -> Instruccion POP    
            case "POP":
                POP(linea.get(2));
                break;
            /////////// -> Instruccion PUSH
            case "PUSH":
                PUSH(linea.get(2));
                break;
            /////////// -> Instruccion COPY
            case "COPY":
                COPY(Integer.valueOf(linea.get(2)), Integer.valueOf(linea.get(4)));
                break;
            /////////// -> Instruccion JMP
            case "JMP":
                JMP(Integer.valueOf(linea.get(2)));
                break;
            /////////// -> Instruccion LOOP
            case "LOOP":
                LOOP(Integer.valueOf(linea.get(2)));
                break;            
            /////////// -> Instruccion INIT
            case "INIT":
                INIT();
                break;
            /////////// -> Instruccion END
            case "END":
                END();
                break;
            /////////// -> Instruccion SUM
            case "SUM":
                SUM(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion SUB
            case "SUB":
                SUB(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion DIV
            case "DIV":
                DIV(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion MUL
            case "MUL":
                MUL(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion AND
            case "AND":
                AND(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion OR
            case "OR":
               OR(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion NOT
            case "NOT":
                NOT(linea.get(2));
                break;    
            /////////// -> Instruccion XOR
            case "XOR":
                XOR(linea.get(2),linea.get(4));
                break;    
            /////////// -> Instruccion CMP
            case "CMP":
                CMP(linea.get(2),linea.get(4));
                break;    
            /////////// -> Instruccion CMPE
            case "CMPE":
                CMPE(linea.get(2),linea.get(4));
                break;    
            /////////// -> Instruccion EQLS
            case "EQLS":
                EQLS(linea.get(2),linea.get(4));
                break;
            /////////// -> Instruccion CON
            case "CON":
                CON(linea.get(2),linea.get(4));
                break;    
        }
    }
    
    /*
     *  Devuelve el valor del registro buscado
     */
    public Object getRegistro(String registro)
    {
        return registros.get(registro);
    }
    
    /*
     *  Devuelve el valor de la cima de la pila
     */
    public Object getMemoria(int i)
    {
        if(memoria.size()<=i)
            return 0;
        else
        return memoria.get(i);
    }
    
    /*
     *  Devuelve el valor de la cima de la pila
     */
    public Object getPila()
    {
        return pila.peek();
    }
    
    /*
     *  Devuelve el valor de CP
     */
    public int getCP()
    {
        return CP;
    }
    
    /*
     *  Devuelve el valor de SP
     */
    public int getSP()
    {
        return SP;
    } 
    
    /*
     *  Devuelve el valor de EP
     */
    public int getEP()
    {
        return EP;
    } 
    
    /*********************************************************************************
    ****************************** Set de Instrucciones ******************************
    *********************************************************************************/

    ///////////////////////////GESTION DE DATOS////////////////////////////////////

    /*
     *   Mueve del registro_a a registro_b      
     */
    public void MOV(String registro_a, String registro_b)
    {
            Object contenido = registros.get(registro_a);
            registros.replace(registro_b, contenido);
    }	
    
    /*
     *   Mueve del registro a la posiciÃ³n de memoria      
     */
    public void MOV(String registro, int memoria)
    {
            Object contenido = registros.get(registro);
            
            if(this.memoria.size()<memoria)
            {
                for(int i=0; i<=memoria+1; i++)
                {
                    this.memoria.add(0);
                }
            }
            
            this.memoria.add(memoria, contenido);
    }
    
    /*
     *   Mueve de la memoria al registro      
     */
    public void MOV(int memoria, String registro)
    {
            Object contenido = this.memoria.get(memoria);
            registros.replace(registro, contenido);
    }
    
    
    /*
     *   Inserta un valor bruto a un registro     
     */
    public void INS(String registro, Object valor)
    {
            registros.replace(registro, valor);
    }
    
    /*
     *		Saca el contenido de la cima de la pila en el registro
     */
    public void POP(String registro)
    {
        registros.replace(registro,pila.pop());	
        SP--;
    }

    /*
     *		Mete el contenido del registro en la pila
     */
    public void PUSH(String registro)
    {
        pila.push(registros.get(registro));
        SP++;
    }

    /*
     *		Copia el contenido de la posicion de memoria a a la b
     */
    public void COPY (int memoria_a, int memoria_b)
    {
        Object valor = memoria.get(memoria_a);
        memoria.add(memoria_b, valor);
    }
    
    /*
     *         Retrocede el CP a la posicion de memoria senialada si JR es true
     */
    public void JMP(int memoria)
    {
        if((boolean)registros.get("JR")) CP=(memoria-2);
    }
    
    /*
     *	       Compara si RL>0 para cambiar la direccion de CP
     */
    public void LOOP(int memoria)
    {
        if((int)registros.get("LR")>0){ 
            CP=(memoria-2);
            registros.replace("LR", (int)registros.get("LR")-1 );
        }
    }
    
    /*
     *	       Introduce el CP en la pila para saber la posicion a la que acudir
     */
    public void INIT()
    {
        pila.push(CP);
    }
    
    /*
     *	       Recupera el valor de CP para volver a la ejecucion
     */
    public void END()
    {
        CP = (int) pila.pop();
    }
    
    ///////////////////////////OPERACIONES NUMERCAS////////////////////////////////////

    /*
     *     Suma el valor de registro_a y registro_b y lo almacena en RR
    */
    public void SUM(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put( "RR", (int)operando1+(int)operando2 );
    }

    /*
     *     Resta el valor de registro_a y registro_b y lo almacena en RR
     */
    public void SUB(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR",(int)operando1-(int)operando2);
    }

    /*
     *     Multiplica el valor de (registro_a x registro_b) y lo almacena en RR
     */
    public void MUL(String registro_a, String registro_b)
    {       
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", (int)operando1*(int)operando2);
    }

    /*
     *     Divide el valor de registro_a entre el de registro_b (a/b) y lo almacena en RR
     */
    public void DIV(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR",(int)operando1/(int)operando2);
    }


     ///////////////////////////OPERACIONES LOGICAS////////////////////////////////////

    /*
     *		Guarda en el registro RR = registro_a & registro_b
     */
    public void AND(String registro_a, String registro_b)
    {
        Object valor1 = registros.get(registro_a);
        Object valor2 = registros.get(registro_b);
        registros.put("RR",(boolean)valor1 & (boolean)valor2);
    }

    /*
     *		Guarda en el registro RR = registro_a | registro_b
     */
    public void OR(String registro_a, String registro_b)
    {
        Object valor1 = registros.get(registro_a);
        Object valor2 = registros.get(registro_b);
        registros.put("RR",(boolean)valor1 | (boolean)valor2);
    }

    /*
     *		invierte el valor del registro, queda en ÃƒÂ©l mismo contenido
     */
    public void NOT(String registro)
    {
        Object valor = registros.get(registro);
        registros.put(registro,!((boolean)valor));
    }
          
    /*
     *		Compara el registro_a != registro_b y lo aloja en RR
     */
    public void XOR(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", operando1!=operando2);
    }
          
    /*
     *		Compara el registro_a > registro_b y lo aloja en RR
     */
    public void CMP(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", (int)operando1>(int)operando2);
    }
          
    /*
     *		Compara el registro_a >= registro_b y lo aloja en RR
     */
    public void CMPE(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", (int)operando1>=(int)operando2);
    }
          
    /*
     *		Compara el registro_a == registro_b y lo aloja en RR
     */
    public void EQLS(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", operando1==operando2);
    }
          
     ///////////////////////////OPERACIONES ALFANUMERICAS////////////////////////////////////

    /*
     *		Concatena el registro_a con el registro_b y lo aloja en RR
     */
    public void CON(String registro_a, String registro_b)
    {
        Object operando1 = registros.get(registro_a);
        Object operando2 = registros.get(registro_b);
        registros.put("RR", (String)operando1+operando2);
    }
}