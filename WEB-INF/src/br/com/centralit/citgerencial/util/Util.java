package br.com.centralit.citgerencial.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.citframework.util.UtilStrings;


/**
 * @author tellus SA
 */
public class Util implements Serializable {
	public static int DIA = 1; 
	public static int MES = 2;
	public static int ANO = 3;
	
	private static final String STMT_EXISTE
				= "SELECT * FROM T#";
	
	public static byte[] gerarHash(String frase, String algoritmo) {
		try {
			MessageDigest md = MessageDigest.getInstance(algoritmo);
			md.update(frase.getBytes());
	    	return md.digest();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static Collection existe(String tabela, String condicao, Class classe, String nomeCampoId) throws Exception, SQLException {
		PreparedStatement stmt;	
		ResultSet rs;
		Collection col = new ArrayList();
		ConexaoBD conexao = new ConexaoBD();
		String sql = "";
		//System.out.println(tabela);
		if(!tabela.equalsIgnoreCase("")){
			if(condicao.equalsIgnoreCase("")){
				sql = STMT_EXISTE.replaceAll("T#", tabela);
			}else{
				sql = STMT_EXISTE.replaceAll("T#", tabela + " WHERE " + condicao);
			}
		}
		try {
		    /* IN�CIO - ALTERA��O DO SQL QUANDO ESTE POSSUI PAR�METROS DO TIPO DATA */
		    List datas = new ArrayList();
		    String regex = "(.|..)/(.|..)/(.|..|...|....)";
		    if(sql.matches("(.*)" + regex + "(.*)")){
    	        String novoSQL = sql.replaceAll("'" + regex + "'", "?");
    	        int posI = 0;  int posF = 0;
    	        String aux = new String(sql);
    	        while(aux.indexOf("'") > 0){
    	            posI = aux.indexOf("'") + 1;
    	            aux = aux.substring(posI);
    	            posF = aux.indexOf("'") + 1;
    	            if(aux.substring(0, posF - 1).matches(regex))
    	                datas.add(aux.substring(0, posF - 1));
    	            aux = aux.substring(posF);
    	        }
    	        sql = novoSQL;
		    }
		    /* FINAL -------------------------------------------------------------- */
		    
			stmt = conexao.getConn().prepareStatement(/*"SET DATEFORMAT DMY;" + */
			        sql);
			if(!datas.isEmpty()){
			    int i = 1;
			    for(Object d : datas){
                    stmt.setDate(i++, new java.sql.Date(formatStringToDate(
                            d.toString()).getTime()));
                }
			}
			rs = stmt.executeQuery();
			
			while(rs.next()){
				Class[] parms = new Class[2];
				parms[0] = ConexaoBD.class;
				parms[1] = int.class;
				
				Constructor constructor = null;
				
				try {
					constructor = classe.getDeclaredConstructor(parms);
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
				
				Object[] parmsConst = new Object[2];
				//parmsConst[0] = conexao;
				parmsConst[0] = null;
				parmsConst[1] = new Integer(rs.getInt(nomeCampoId));
				
				try {
				
					Object o = constructor.newInstance(parmsConst);
					col.add(o);
					
				} catch (IllegalArgumentException e2) {
					e2.printStackTrace();
				} catch (InstantiationException e2) {
					e2.printStackTrace();
				} catch (IllegalAccessException e2) {
					e2.printStackTrace();
				} catch (InvocationTargetException e2) {
					e2.printStackTrace();
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e){
		    e.printStackTrace();
		    //if (e.getMessage().matches("Syntax error converting")){
		    //}else{
			//	e.printStackTrace();
			//	throw new Exception("sis0001", e.getMessage());
		    //}
		}
		
		conexao.fecharConexao();
		conexao = null;
		return col;
	}
	
	//Time constants (in milliseconds)
	private static final long SECOND = 1000;
	private static final long MINUTE = 60 * SECOND;
	private static final long HOUR   = 60 * MINUTE;
	private static final long DAY    = 24 * HOUR;
	private static final long WEEK   = 7 * DAY;
	private static final SimpleDateFormat dateFormatter =
		new SimpleDateFormat("EEEE, MMM d 'as' h:mm a");
	private static final SimpleDateFormat yesterdayFormatter =
		new SimpleDateFormat("'Ont�m ' h:mm a");	
	/**
	 * Dependendo do hor�rio retorna a mensagem de saudacao.
	 * @return
	 */
	public static final String saudacao(){
		Calendar time = Calendar.getInstance();
		String retorno = null;
		if (time.get(Calendar.HOUR_OF_DAY) >= 6 && time.get(Calendar.HOUR_OF_DAY) < 12){
			retorno = "Bom dia, ";
		} else {
			if (time.get(Calendar.HOUR_OF_DAY) >= 12 && time.get(Calendar.HOUR_OF_DAY) < 18){
				retorno = "Boa tarde, ";
			} else {
				retorno = "Boa noite, ";
			}
		}
		return retorno;
	}
	/**
	 * Reencaminha para a URL passada como par�metro. 
	 * @param url
	 */
	public static final void ReencaminhaURL(HttpServletRequest request, HttpServletResponse response, String url)
						throws ServletException, IOException{
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request,response);
	}
	
	/**
	 * define o nome do objeto para retorno.
	 * @param obj
	 * @param acao
	 * @return
	 */
	public static final String defineNomeObjetoRetorno(String obj, String acao){
		return obj + "." + acao + ".RO";
	}
	
	/**
	 * Cria uma cole��o a partir de um array de objetos
	 * @param objetos
	 * @return
	 */
	public static final Collection createCollection(Object[] objetos){
		Collection c = new ArrayList();
		int i;
		
		for (i = 0; i < c.size(); i++){
			c.add(objetos[i]);
		}
		return c;
	}
	
	/**
	 * Retorna informa��es do dia
	 * @return
	 */
	public static final String getInformacoesDia(){
		String retorno = null;
		Calendar hoje = Calendar.getInstance();
		String diaSemana = getDiaSemana(hoje);
		String nomeMes = getNomeMes(hoje.get(Calendar.MONTH));
		
		retorno = diaSemana + ", " + hoje.get(Calendar.DAY_OF_MONTH) + " de " + nomeMes + " de " + hoje.get(Calendar.YEAR);
		return retorno;
	}
	
	/**
	 * Pega a atual (hoje)
	 * @return
	 */
	public static final java.util.Date getDataAtual() {
		Calendar hoje = Calendar.getInstance();
		return hoje.getTime();
	}
	
	/**
	 * Retorna o nome do m�s
	 * @param mes
	 * @return
	 */
	public static final String getNomeMes(int mes){
		switch (mes){
			case Calendar.JANUARY:
				return "Janeiro";
			case Calendar.FEBRUARY:
				return "Fevereiro";
			case Calendar.MARCH:
				return "Mar�o";
			case Calendar.APRIL:
				return "Abril";
			case Calendar.MAY:
				return "Maio";
			case Calendar.JUNE:
				return "Junho";
			case Calendar.JULY:
				return "Julho";
			case Calendar.AUGUST:
				return "Agosto";
			case Calendar.SEPTEMBER:
				return "Setembro";
			case Calendar.OCTOBER:
				return "Outubro";
			case Calendar.NOVEMBER:
				return "Novembro";
			case Calendar.DECEMBER:
				return "Dezembro";

		}
		return null;
	}
	/**
	 * Obtem o dia da semana
	 * @param dia
	 * @return
	 */
	public static final String getDiaSemana(Calendar dia){
		switch (dia.get(Calendar.DAY_OF_WEEK)){
			case Calendar.SUNDAY:
				return "Domingo";
			case Calendar.MONDAY:
				return "Segunda-feira";
			case Calendar.TUESDAY:
				return "Ter�a-feira";
			case Calendar.WEDNESDAY:
				return "Quarta-feira";
			case Calendar.THURSDAY:
				return "Quinta-feira";
			case Calendar.FRIDAY:
				return "Sexta-feira";
			case Calendar.SATURDAY:
				return "Sabado";
		}
		return null;
	}
	/**
	 * Formata HTML, segundo caracteres especiais
	 * @param string
	 * @return
	 */
	public static final String encodeHTML(String string){
		if (string == null){
			return null;
		}
		char c;
		int length = string.length();
		StringBuffer encoded = new StringBuffer(2 * length);
		for (int i=0; i < length; i++){
			c = string.charAt(i);
			if (c == '�')
				encoded.append("&ccedil;");
			//else if (c == '"')
			//	encoded.append("&quot;");
			else if (c == '�')
				encoded.append("&Ccedil;");
			else if (c == '�' || c == '�' || c == '�' || c == '�' || 
			         c == '�' || c == '�' || c == '�' || c == '�' ||
			         c == '�' || c == '�')
				encoded.append("&" + getLetraCorrespondente(c) + "acute;");
			else if (c == '�' || c == '�' || c == '�' || c == '�' || 
					 c == '�' || c == '�' || c == '�' || c == '�' ||
					 c == '�' || c == '�')
				encoded.append("&" + getLetraCorrespondente(c) + "circ;");
			else if (c == '�' || c == '�' || c == '�' || c == '�') 
				encoded.append("&" + getLetraCorrespondente(c) + "tilde;");
			else 
				encoded.append(c);
		}
		return Util.replaceInvalid(encoded.toString());
	}
	
	/**
	 * Pega a letra correspondente ao caracter.
	 * @param c
	 * @return
	 */
	public static String getLetraCorrespondente(char c){
		if (c == '�' || c == '�' || c == '�'){
			return "a";
		}
		else if (c == '�' || c == '�' || c == '�'){ 
			return "A";
		}
		else if (c == '�' || c == '�'){
			return "e";
		}
		else if (c == '�' || c == '�'){
			return "E";
		}
		else if (c == '�' || c == '�'){
			return "i";
		}
		else if (c == '�' || c == '�'){
			return "I";
		}
		else if (c == '�' || c == '�' || c == '�'){
			return "o";
		}
		else if (c == '�' || c == '�' || c == '�'){
			return "O";
		}
		else if (c == '�' || c == '�'){
			return "u";
		}
		else if (c == '�' || c == '�'){
			return "U";
		}
		else {
			char auxChar[] = new char[1];
			auxChar[0] = c;
			String aux = new String(auxChar); 
			return aux;
		}
	}
	
	/**
	 * Converte o texto para HTML
	 * @param textoConverter
	 * @return
	 */
	public static String converterTextoInstitucionalHTML(String textoConverter){
		String retorno;
		
		retorno = textoConverter;  
		retorno = retorno.replaceAll("&#39;",(String)""+(char)39);
		retorno = retorno.replaceAll("left","justify");
		retorno = retorno.replaceAll("<P>","<p align='justify'>");
		retorno = retorno.replaceAll("MARGIN-RIGHT:","text-align:justify:");
		retorno = retorno.replaceAll(": 0px","");
		retorno = retorno.replaceAll("<A href=\"file://","<A Target=_blank href=\"http://");
		
		retorno = Util.encodeHTML(retorno);
		
		return retorno;
	}
	
	public static String converterTextoHTML(String textoConverter){
		return converterTextoInstitucionalHTML(textoConverter);	
	}
	
	public static String converterHtmlText(String texto){
		String retorno;
		
		retorno = texto;  
		retorno = retorno.replaceAll("&#34;", (String)""+(char)34);
		retorno = retorno.replaceAll("<br>", (String)""+(char)10);
		retorno = retorno.replaceAll("<BR>", (String)""+(char)10);
		retorno = retorno.replaceAll("<Br>", (String)""+(char)10);
		retorno = retorno.replaceAll("<bR>", (String)""+(char)10);
		
		return retorno;
	}

	public static String converterHtmlQuadro(String texto){
		String retorno;
		
		retorno = texto;  
		retorno = retorno.replaceAll("&#34;", (String)""+(char)34);
		retorno = retorno.replaceAll("<A href=\"file://", "<A Target=_blank href=\"http://");
		
		return retorno;
	}
	
	public static String converterParaHD(String texto){
		String retorno;
		
		if (texto == null){
			return "";
		}
		
		retorno = texto;  
		retorno = retorno.replaceAll((String)""+(char)34, "&#34;");
		retorno = retorno.replaceAll((String)""+(char)10, "<br>");
		retorno = retorno.replaceAll((String)""+(char)39, "&#39;");
		
		retorno = encodeHTML(retorno);
		
		return retorno;
	}
	
	/**
	 * gera o nome da pasta, retirando os caracteres n�o utiliizaveis.
	 * @param texto
	 * @return
	 */
	public static String getNomePasta(String texto){
		return removeSpecialChar(texto);
	}
	/**
	 * Remove os caracteres especiais.
	 * @param texto
	 * @return
	 */
	public static String removeSpecialChar(String texto){
		String string;
		
		int i;
		char letra;
		string = texto;
		String textoValido = "";
		 for(i=0;i < string.length();i++){
			letra = string.charAt(i);
			if (letra == ' ' || letra == '>' || letra == '<' || letra == '*' || letra == '|' || letra == '\\' ||
			    letra == '/' || letra == ':' || letra == '&' || letra == '#' || letra == '$' || letra == '@'  ||
				letra == '!' || letra == '"' || letra == '\'' || letra == '?' || letra == ';'){
				textoValido = textoValido + '_';
			} else {
				textoValido = textoValido + Util.getLetraCorrespondente(letra);
			}
		 }
		return textoValido;
	}
	/**
	 * Apaga os diretorios e subdiretorios informados
	 * @param path
	 * @return
	 */
	public static boolean deleteDiretorioAndSubdiretorios(String path){
		File diretorio = new File(path);
		File fileDir;
		
		if (diretorio.isDirectory()){
			if (diretorio.exists()){
				File[] filesDiretorios = diretorio.listFiles();
				for (int i = 0; i < filesDiretorios.length; i++){
					fileDir = filesDiretorios[i];
					if (fileDir.isDirectory()){
						deleteDiretorioAndSubdiretorios(fileDir.getPath());
					} else {
						fileDir.delete();
					}
				}
				return diretorio.delete();
			}
		}
		
		return false;
	}
	
	/**
	 * Cria diretorio, caso n�o exista.
	 * @param path
	 * @return
	 */
	public static boolean createDiretorio(String path){
		File diretorio = new File(path);
		if (!diretorio.exists()){
			return diretorio.mkdir();
		}
		return true;
	}
		
	/**
	 * Apaga o arquivo especificado
	 * @param arquivo
	 * @return
	 */
	public static boolean deleteFile(String arquivoStr){
		File arquivo = new File(arquivoStr);
		if (arquivo.isFile()){
			return arquivo.delete();
		}
		return false;
	}
	
	/**
	 * Pega o nome do arquivo
	 * @return
	 */
	public static String getNameFile(String fullPathFile){
		int tam = fullPathFile.length() - 1;
		String nomeFile = "";
		for (int i = tam; i >= 0; i--){
			if (fullPathFile.charAt(i) == '\\' || fullPathFile.charAt(i) == '/')
				break;
			else
				nomeFile = fullPathFile.charAt(i) + nomeFile;
		}
		return nomeFile;
	}	
	public static String replaceInvalid(String string){
		String retorno = "";
		retorno = string.replace((char)150,'-');
		retorno = retorno.replace((char)147,'"');
		retorno = retorno.replace((char)148,'"');
		retorno = retorno.replace((char)8220,'"');
		retorno = retorno.replace((char)8221,'"');
		retorno = retorno.replace((char)8211,'-');
		retorno = retorno.replace((char)8217,' ');
			
		return retorno;
	}
	
	

	public static String pegaCor(String qualAba, String aba){
		String cor = "#F2F2F2";
		if(qualAba.equalsIgnoreCase("receita")){
			if(aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("procedimentos")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}	
		}else if(qualAba.equalsIgnoreCase("anamnese")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("laudo")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("dietas")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("imagens")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("cid")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("evolucao")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("atestados")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}else if(qualAba.equalsIgnoreCase("declaracoes")){
			if (aba.equalsIgnoreCase(qualAba)){
				cor = "#99CCCC";
			}
		}

		return cor;
  	}
	/**
	 * verifa
	 * @param qualAba
	 * @param aba
	 * @param fecha
	 * @return
	 */
	public static String pegaNegrito(String qualAba, String aba, boolean fecha){
		String cor = "";
		if(qualAba.equalsIgnoreCase("receita")){
			if(aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("procedimentos")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}	
		}else if(qualAba.equalsIgnoreCase("anamnese")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("laudo")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("dietas")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("imagens")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("cid")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("evolucao")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("atestados")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}else if(qualAba.equalsIgnoreCase("declaracoes")){
			if (aba.equalsIgnoreCase(qualAba)){
				if (fecha){
					cor = "</strong>";
				}else{
					cor = "<strong>";
				}
			}
		}
		
		return cor;
  	}

	/**
     * Formata a string no padr�o DD/MM/YYYY em Date.
     * @param data
     * @return
     */
    public static Date formatStringToDate(String data)
    {
        if(!UtilStrings.isNotVazio(data)) return null;
        try{
            return new SimpleDateFormat("dd/MM/yyyy").parse(data);
        }catch(ParseException pe){
            return null;
        }
    }
	
	/**
	 * Formata a data com DD/MM/YYYY.
	 * @param data
	 * @return
	 */
	public static String formatDateDDMMYYYY(Date data){
		if (data == null) return "";
	    
	    Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int diaMes = c.get(Calendar.DAY_OF_MONTH);
		int mesAno = c.get(Calendar.MONTH) + 1;
		int ano = c.get(Calendar.YEAR);
		
		String retorno;
		
		NumberFormat formato2 = new DecimalFormat("00");
		NumberFormat formato4 = new DecimalFormat("0000");
		retorno = formato2.format(diaMes);
		retorno = retorno + "/";
		retorno = retorno + formato2.format(mesAno);
		retorno = retorno + "/";
		retorno = retorno + formato4.format(ano);
		
		return retorno;
	}

	/**
	 * Formata a data com MM/DD/YYYY.
	 * @param data
	 * @return
	 */
	public static String formatDateMMDDYYYY(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int diaMes = c.get(Calendar.DAY_OF_MONTH);
		int mesAno = c.get(Calendar.MONTH) + 1;
		int ano = c.get(Calendar.YEAR);
		
		String retorno;
		
		NumberFormat formato2 = new DecimalFormat("00");
		NumberFormat formato4 = new DecimalFormat("0000");
		
		retorno = formato2.format(mesAno);
		retorno = retorno + "/";
		retorno = retorno + formato2.format(diaMes);
		retorno = retorno + "/";
		retorno = retorno + formato4.format(ano);
		
		return retorno;
	}
	/**
	 * Formata a data com DD/MM/YYYY HH:MM:SS.
	 * @param data
	 * @return
	 */
	public static String formatDateDDMMYYYYHHMMSS(Date data){
		Calendar c = Calendar.getInstance();
		if(data == null){
			return "";
		}
		c.setTime(data);
		
		int diaMes = c.get(Calendar.DAY_OF_MONTH);
		int mesAno = c.get(Calendar.MONTH) + 1;
		int ano = c.get(Calendar.YEAR);
		int hora = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
				
		String retorno;
		
		NumberFormat formato2 = new DecimalFormat("00");
		NumberFormat formato4 = new DecimalFormat("0000");
		retorno = formato2.format(diaMes);
		retorno = retorno + "/";
		retorno = retorno + formato2.format(mesAno);
		retorno = retorno + "/";
		retorno = retorno + formato4.format(ano);
		retorno = retorno + " ";
		retorno = retorno + formato2.format(hora);
		retorno = retorno + ":";
		retorno = retorno + formato2.format(min);
		retorno = retorno + ":";
		retorno = retorno + formato2.format(sec);
		
		return retorno;
	}

	/**
	 * Formata a data com DD de MMM de YYYY.
	 * @param data
	 * @return
	 */
	public static String formatDateDDMMMYYYY(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int diaMes = c.get(Calendar.DAY_OF_MONTH);
		int mesAno = c.get(Calendar.MONTH);
		int ano = c.get(Calendar.YEAR);
		
		String retorno;
		
		NumberFormat formato2 = new DecimalFormat("00");
		NumberFormat formato4 = new DecimalFormat("0000");
		retorno = formato2.format(diaMes);
		retorno = retorno + " de ";
		retorno = retorno + getNomeMes(mesAno);
		retorno = retorno + " de ";
		retorno = retorno + formato4.format(ano);
		
		return retorno;
	}
	public static int getYear(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		return c.get(Calendar.YEAR);
	}
	public static int getMonth(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		return c.get(Calendar.MONTH) + 1;
	}
	public static int getDay(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		return c.get(Calendar.DAY_OF_MONTH);
	}		
	public static String getDayOfWeek(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int dia = c.get(Calendar.DAY_OF_WEEK);
		switch (dia){
			case Calendar.SUNDAY:
				return "Domingo";
			case Calendar.MONDAY:
			    return "Segunda-feira";
			case Calendar.TUESDAY:
			    return "Ter�a-feira";
			case Calendar.WEDNESDAY:
			    return "Quarta-feira";
			case Calendar.THURSDAY:
			    return "Quinta-feira";
			case Calendar.FRIDAY:
			    return "Sexta-feira";
			case Calendar.SATURDAY:
			    return "Sab�do";
		}
		return "";
	}
	public static String getDayOfWeekResumido(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int dia = c.get(Calendar.DAY_OF_WEEK);
		switch (dia){
			case Calendar.SUNDAY:
				return "Domingo";
			case Calendar.MONDAY:
			    return "Segunda";
			case Calendar.TUESDAY:
			    return "Ter�a";
			case Calendar.WEDNESDAY:
			    return "Quarta";
			case Calendar.THURSDAY:
			    return "Quinta";
			case Calendar.FRIDAY:
			    return "Sexta";
			case Calendar.SATURDAY:
			    return "Sab�do";
		}
		return "";
	}	
	public static String getDayWeekNameOfNumber(int day){
		switch (day){
			case 1:
				return "Domingo";
			case 2:
			    return "Segunda";
			case 3:
			    return "Ter�a";
			case 4:
			    return "Quarta";
			case 5:
			    return "Quinta";
			case 6:
			    return "Sexta";
			case 7:
			    return "S�bado";
		}
		return "";
	}	
	public static String getDayOfWeekSigla(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int dia = c.get(Calendar.DAY_OF_WEEK);
		switch (dia){
			case Calendar.SUNDAY:
				return "Dom";
			case Calendar.MONDAY:
			    return "Seg";
			case Calendar.TUESDAY:
			    return "Ter";
			case Calendar.WEDNESDAY:
			    return "Qua";
			case Calendar.THURSDAY:
			    return "Qui";
			case Calendar.FRIDAY:
			    return "Sex";
			case Calendar.SATURDAY:
			    return "Sab";
		}
		return "";
	}	
	public static int getDayNumberOfWeekBySigla(String sigla){
		if ("DOM".equalsIgnoreCase(sigla)){
			return 1;
		}else if ("SEG".equalsIgnoreCase(sigla)){
			return 2;
		}else if ("TER".equalsIgnoreCase(sigla)){
			return 3;
		}else if ("QUA".equalsIgnoreCase(sigla)){
			return 4;
		}else if ("QUI".equalsIgnoreCase(sigla)){
			return 5;
		}else if ("SEX".equalsIgnoreCase(sigla)){
			return 6;
		}else if ("SAB".equalsIgnoreCase(sigla)){
			return 7;
		}
		return 0;
	}	
	public static int getDayOfWeekNumber(Date data){
		Calendar c = Calendar.getInstance();
		
		c.setTime(data);
		
		int dia = c.get(Calendar.DAY_OF_WEEK);
		switch (dia){
			case Calendar.SUNDAY:
				return 1;
			case Calendar.MONDAY:
			    return 2;
			case Calendar.TUESDAY:
			    return 3;
			case Calendar.WEDNESDAY:
			    return 4;
			case Calendar.THURSDAY:
			    return 5;
			case Calendar.FRIDAY:
			    return 6;
			case Calendar.SATURDAY:
			    return 7;
		}
		return 0;
	}	
	/**
	 * Formata a data com DD/MM/YYYY.
	 * @param data
	 * @return
	 */
	public static String converteDataUtilToString(Date data){
		String retorno = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		retorno = sdf.format(data);
		return retorno;
	}
	
	/**
	 * Formata a data com DD de MMM de YYYY.
	 * @param data
	 * @return
	 */
	public static String converteDataUtilToStringSpecial(Date data){
		String retorno = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd' de 'MMM' de 'yyyy");
		retorno = sdf.format(data);
		return retorno;
	}
	/**
	 * Formata a data em uma String conforme o padr�o especificado na vari�vel pattern.
	 * @param data
	 * @return
	 */
	public static String converteDataUtilToString(Date data, String pattern){
		String retorno = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		retorno = sdf.format(data);
		return retorno;
	}
	
	/**
	 * Transforma uma String em java.util.Date.
	 * O padr�o da String deve estar especificado na atributo pattern
	 * conforme documenta��o da classe SimpleDateFormat
	 * @param data
	 * @param pattern
	 * @return
	 */
	public static Date converteStringToDataUtil(String data, String pattern) throws ParseException{
		Date retorno = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		retorno = sdf.parse(data);
		return retorno;
	}
	/**
	 * Transforma uma String em java.util.Date.
	 * O padr�o da String deve estar especificado na atributo pattern
	 * conforme documenta��o da classe SimpleDateFormat
	 * @param data
	 * @param pattern
	 * @return
	 */
	public static java.sql.Date converteStringToDataSQL(String data, String pattern) throws ParseException{
		Date retorno = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		retorno = sdf.parse(data);
		return new java.sql.Date(retorno.getTime());
	}	
	/**
	 * Pega o nome do diretorio, retirando o nome do arquivo.
	 * @param pathNameDiretorio
	 * @return
	 */
	public static String getPathRelativo(String pathNameDiretorio){
		char c;
		int length = pathNameDiretorio.length();
		
		for (int i=length-1; i >= 0; i--){
			c = pathNameDiretorio.charAt(i);
			if (c == '/'){
				return pathNameDiretorio.substring(0, i);
			}
		}
		return "";
	}
	
	/**
	 * Faz replace de ../ para /ouvidoria/
	 * @param pathNameDiretorio
	 * @return
	 */
	public static String replacePathOuvidoria(String pathNameDiretorio){
		if (pathNameDiretorio == null){
			return "";
		}
		String retorno = "";
		
		if (pathNameDiretorio.substring(0,3).trim().equals("../")){
			retorno = pathNameDiretorio.replaceFirst("../", "/ouvidoria/");
		}
		return retorno;
	}
	
	/**
	 * Retorna o tipo do arquivo pela extens�o
	 * @param fileName
	 * @return
	 */
	public static String getFileType(String fileName){
		String ext = getFileExtension(fileName);
		if (ext.toUpperCase().equals("DOC")){
			return "Word Document";
		} else if(ext.toUpperCase().equals("XLS")){
			return "Excel Document";
		} else if(ext.toUpperCase().equals("HTML")){
			return "Html Document";
		} else if(ext.toUpperCase().equals("HTM")){
			return "Html Document";
		} else if(ext.toUpperCase().equals("TXT")){
			return "Text File";
		} else {
			return ext + " file";
		}
	}
	/**
	 * Obtem a extens�o de um arquivo.
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName){
		String ext = "";
		boolean encontrado = false;
		for (int i = fileName.length() - 1; i > 0; i--){
			if (fileName.charAt(i) == '.'){
				encontrado = true;
				break;
			} else {
				ext = fileName.charAt(i) + ext;
			}
		}
		if (encontrado){
			return ext;
		} else {
			return "";
		}
	}
	/**
	 * Gera os itens da combo.
	 * @param col
	 * @param classe
	 * @param campoId
	 * @param campoDesc
	 * @param valor
	 * @return
	 * @throws Exception
	 */	
	public static StringBuffer createItemsCombo(Collection col, Class classe, String campoId, String campoDesc, int valor) throws Exception{
		StringBuffer retorno = new StringBuffer();
		Iterator it = col.iterator();
		Object objeto, objAux, objAux2;
		Integer intAux;
		String strAux, aux;
		int iAux;
		Method metodo = null;
		
		while(it.hasNext()){
			objeto = it.next();
			try{
				campoId = campoId.substring(0,1).toUpperCase()+campoId.substring(1);
				campoDesc = campoDesc.substring(0,1).toUpperCase()+campoDesc.substring(1);
				metodo = classe.getMethod("get" + campoId ,null);
				objAux = metodo.invoke(objeto, null);
				
				metodo = classe.getMethod("get" + campoDesc ,null);
				objAux2 = metodo.invoke(objeto, null);
				
				intAux = (Integer)objAux;
				strAux = (String)objAux2;
				
				iAux = intAux.intValue();
				aux = "";
				if (iAux == valor){
					aux = "selected";
				}
				retorno.append("<option " + aux + " value=\"" + iAux + "\">" + strAux + "</option>\n");
			} catch (SecurityException e){
				e.printStackTrace();
				throw new Exception();
			} catch (NoSuchMethodException e){
				e.printStackTrace();
				throw new Exception();
			} catch (IllegalArgumentException e){
				e.printStackTrace();
				throw new Exception();
			} catch (IllegalAccessException e){
				e.printStackTrace();
				throw new Exception();
			} catch (InvocationTargetException e){
				e.printStackTrace();
				throw new Exception();
			}
		}
		return retorno;
	}
	
	/**
	 * Gera os itens da combo.
	 * @param col
	 * @param classe
	 * @param campoId
	 * @param campoDesc
	 * @param valor
	 * @return
	 * @throws Exception
	 */	
	public static StringBuffer createItemsComboStrStr(Collection col, Class classe, String campoId, String campoDesc, String valor) throws Exception{
		StringBuffer retorno = new StringBuffer();
		Iterator it = col.iterator();
		Object objeto, objAux1, objAux2;
		String strAux1;
		String strAux2, aux;
		Method metodo = null;
		
		while(it.hasNext()){
			objeto = it.next();
			try{
				campoId = campoId.substring(0,1).toUpperCase()+campoId.substring(1);
				campoDesc = campoDesc.substring(0,1).toUpperCase()+campoDesc.substring(1);
				metodo = classe.getMethod("get" + campoId ,null);
				objAux1 = metodo.invoke(objeto, null);
				
				metodo = classe.getMethod("get" + campoDesc ,null);
				objAux2 = metodo.invoke(objeto, null);
				
				strAux1 = "" + objAux1;
				strAux2 = (String)objAux2;

				aux = "";
				if (strAux1.trim().equalsIgnoreCase(valor.trim())){
					aux = "selected";
				}
				retorno.append("<option " + aux + " value=\"" + strAux1 + "\">" + strAux2 + "</option>\n");
			} catch (SecurityException e){
				e.printStackTrace();
				throw new Exception();
			} catch (NoSuchMethodException e){
				e.printStackTrace();
				throw new Exception();
			} catch (IllegalArgumentException e){
				e.printStackTrace();
				throw new Exception();
			} catch (IllegalAccessException e){
				e.printStackTrace();
				throw new Exception();
			} catch (InvocationTargetException e){
				e.printStackTrace();
				throw new Exception();
			}
		}
		return retorno;
	}
	
	/**
	 * Verifica se o param � null para substituir por ""
	 * @param conteudo
	 * @return
	 */
	public static String nullPorString(String conteudo){
		String string = "";
		
		string = conteudo == null ? "" : conteudo;
	
		return string;
	}
	
	/**
	 * Transforma a hora em formato 0000
	 * @param horaPar
	 * @return
	 */	
	public static String transformHora(String horaPar){
		String horaAux = horaPar;
		if (horaAux == null) horaAux = "0000";
		horaAux = horaAux.trim();
		String newHora = "";
		int i = 0;
		for(i = 0; i < horaAux.length(); i++){
			if (horaAux.charAt(i) != ':'){
				newHora = newHora + horaAux.charAt(i);
			}
		}
		if (newHora.length() < 4){
			for(i = newHora.length() - 1; i < 3; i++){
				newHora = "0" + newHora;
			}
		}
		return newHora;
	}
	

	
	/**
	 * Formata a hora em formato 00:00
	 * @param horaPar
	 * @return
	 */	
	public static String formatHora(String horaPar){
		String newHora = "";
		if (horaPar.length() == 3){
			newHora = "0" + horaPar.substring(0,1) + ":" + horaPar.substring(1,3);
		}else{
			newHora = horaPar.substring(0,2) + ":" + horaPar.substring(2,4);
		}
		return newHora;
	}
	
	
	public static double getHoraDbl(String hora) {
		if (hora == null)
			return 0.0;
		String horaParm = hora.trim();
		int tam = horaParm.length();
		double horas = new Double(horaParm.substring(0, tam-2)).intValue();
		double minutos = new Double(horaParm.substring(tam-2, tam));
		return horas + (minutos/60);
	}

	public static String getHoraStr(double hora) {
		double horaParm = hora;
		if (horaParm < 0)
				horaParm = horaParm * -1;
		long s = Math.round (horaParm * 3600);
		long h = s / 3600;
		long m = (s - 3600 * h) / 60;
		String result = "";
		if (h < 10)
		result += "0";
		result += h + "";
		if (m < 10)
		result += "0";
		result += m + "";
		return result;
	}

	
	/**
	 * Obtem o valor do identificador que est� na requisi��o.
	 * @param request
	 * @param identificador
	 * @return
	 */
	public static String getStringRequest(HttpServletRequest request, String identificador){
		String aux = (String) request.getAttribute(identificador);
		if (aux == null){ //Se getParameter for nulo, tenta pegar em getAttribute.
			aux = (String) request.getParameter(identificador);
		}
		if (aux == null) aux = "";
		return aux;
	}
	public static String getStringRequest(HttpServletRequest request, String identificador, int i){
		String aux = (String) request.getAttribute(identificador);
		if (aux == null){ //Se getParameter for nulo, tenta pegar em getAttribute.
			aux = (String) request.getParameter(identificador);
		}
		if (aux == null) aux = "";
		
		String[] arrayReq = aux.split(Constantes.CARACTER_SEPARADOR);
		if (arrayReq != null){
		    if (arrayReq.length > i){
		        return arrayReq[i];
		    }
		}
		return null;
	}
	public static int getLenStringRequest(HttpServletRequest request, String identificador){
		String aux = (String) request.getAttribute(identificador);
		if (aux == null){ //Se getParameter for nulo, tenta pegar em getAttribute.
			aux = (String) request.getParameter(identificador);
		}
		if (aux == null) aux = "";
		
		String[] arrayReq = aux.split(Constantes.CARACTER_SEPARADOR);
		if (arrayReq != null){
		    try{
		        return arrayReq.length;
		    }catch(Exception e){
		        return 0;
		    }
		}
		return 0;
	}	
	
	public static boolean horarioExisteEmCollecao(String horaInicial, String horaFinal, Collection col){
		if (col == null){
			return false;
		}
		if (col.isEmpty()){
			return false;
		}
		int horaInicialInt = Integer.parseInt("0" + Util.transformHora(horaInicial.trim()));
		int horaFinalInt = Integer.parseInt("0" + Util.transformHora(horaFinal.trim()));
		Iterator itAux = col.iterator();
		String[] valor;
		int valor1;
		int valor2;
		while(itAux.hasNext()){
			valor = ((String)itAux.next()).split("@");
			valor1 = Integer.parseInt("0" + Util.transformHora(valor[0]));
			valor2 = Integer.parseInt("0" + Util.transformHora(valor[1]));
			if (horaInicialInt >= valor1 && horaInicialInt < valor2){
				return true;
			}
			if (horaFinalInt > valor1 && horaFinalInt <= valor2){
				return true;
			}	
			if (horaInicialInt <= valor1 && horaFinalInt >= valor2){
				return true;
			}			
		}
		return false;
	}
	
	public static String generateHTMLHidden(String nome, String valor){
		return "<input type='hidden' name='" + nome + "' value='" + valor + "'>";
	}
	
	public static String generateHTMLHidden(HttpServletRequest request, Collection colIds){
		String acum = "";
		String nome = "";
		Iterator itAux = colIds.iterator();
		while(itAux.hasNext()){
			nome = (String)itAux.next();
			if (!acum.equalsIgnoreCase("")){
				acum = acum + "\n";
			}
			acum = acum + Util.generateHTMLHidden(nome, Util.nullPorString(Util.getStringRequest(request, nome)));
		}
		return acum;
	}
	
	public static String generateHTMLOptions(String strValores, String charSep, String defaultValue, String defaultStr){
		String[] auxStr = strValores.split(charSep);
		String saida = "";
		for(int i = 0; i < auxStr.length; i++){
		    if (!Util.nullPorString(auxStr[i]).trim().equalsIgnoreCase("")){
		        saida = saida + "<option value=\"" + i + "\">" + auxStr[i] + "</option>\n";
		    }
		}
		if (saida.equalsIgnoreCase("")){
			saida = "<option value=\"" + defaultValue + "\">" + defaultStr + "</option>\n";
		}
		return saida;
	}
	
	public static String dateToText( Date date ) {
		if( date == null ) {
			return "";
		}

		long delta = System.currentTimeMillis() - date.getTime();

		// within the last hour
		if( (delta / HOUR) < 1 ) {
			long minutes = (delta/MINUTE);
			if( minutes == 0 ) {
				return "menos de 1 minuto atr�s";
			}
			else if( minutes == 1 ) {
				return "1 minuto atr�s";
			}
			else {
				return ( minutes + " minutos atr�s" );
			}
		}

		// sometime today
		if( (delta / DAY) < 1 ) {
			long hours = (delta/HOUR);
			if( hours <= 1 ) {
				return "1 hora atr�s";
			}
			else {
				return ( hours + " horas atr�s" );
			}
		}

		// within the last week
		if( (delta / WEEK) < 1 ) {
			double days = ((double)delta/(double)DAY);
			if( days <= 1.0 ) {
				return yesterdayFormatter.format(date);
			}
			else {
				return dateFormatter.format(date);
			}
		}

		// before a week ago
		else {
			return dateFormatter.format(date);
		}
	}
	
	public static String tiraEspacos(String nomePar){
	    int i;
	    String strSaida = "";
	    for (i =0; i < nomePar.length(); i++){
	        if ( nomePar.charAt(i) != ' ' ){
	            strSaida = strSaida + nomePar.charAt(i);
	        }
	    }
	    return strSaida;
	}
	public static String generateNomeBusca(String nomePar){
	    int i;
	    String strSaida = "";
	    for (i =0; i < nomePar.length(); i++){
	        if ( IsValidCharFind(nomePar.charAt(i)) ){
	            strSaida = strSaida + nomePar.charAt(i);
	        }else{
	            strSaida = strSaida + ChangeCharInvalid(nomePar.charAt(i));
	        }
	    }
	    return strSaida;
	}
	public static boolean IsValidCharFind(char c){
	    switch(c){
	    	case 'a':
	    	case 'b':
	    	case 'c':
	    	case 'd':
	    	case 'e':
	    	case 'f':
	    	case 'g':
	    	case 'h':
	    	case 'i':
	    	case 'j':
	    	case 'k':
	    	case 'l':
	    	case 'm':
	    	case 'n':
	    	case 'o':
	    	case 'p':
	    	case 'q':
	    	case 'r':
	    	case 's':
	    	case 't':
	    	case 'u':
	    	case 'v':
	    	case 'w':
	    	case 'x':
	    	case 'y':
	    	case 'z':
	    	case 'A':
	    	case 'B':
	    	case 'C':
	    	case 'D':
	    	case 'E':
	    	case 'F':
	    	case 'G':
	    	case 'H':
	    	case 'I':
	    	case 'J':
	    	case 'K':
	    	case 'L':
	    	case 'M':
	    	case 'N':
	    	case 'O':
	    	case 'P':
	    	case 'Q':
	    	case 'R':
	    	case 'S':
	    	case 'T':
	    	case 'U':
	    	case 'V':
	    	case 'W':
	    	case 'X':
	    	case 'Y':
	    	case 'Z':
	    	case '%':
	    	    return true;
	    }
	    return false;
	}
	
	/**
	 * Substitui um caracter especial por uma String
	 */
	public static String ChangeCharInvalid(char c){
		if (c == '�' || c == '�' || c == '�'){
			return "a";
		}
		else if (c == '�' || c == '�' || c == '�'){ 
			return "A";
		}
		else if (c == '�' || c == '�'){
			return "e";
		}
		else if (c == '�' || c == '�'){
			return "E";
		}
		else if (c == '�' || c == '�'){
			return "i";
		}
		else if (c == '�' || c == '�'){
			return "I";
		}
		else if (c == '�' || c == '�' || c == '�'){
			return "o";
		}
		else if (c == '�' || c == '�' || c == '�'){
			return "O";
		}
		else if (c == '�' || c == '�'){
			return "u";
		}
		else if (c == '�' || c == '�'){
			return "U";
		}
		else if (c == '�'){
			return "C";
		}
		else if (c == '�'){
			return "c";
		}
		else {
		    return "";
		}
	}
	 
	public static String formataNumero(String sValue, String sMask){
	    int tamCampo = sValue.length();
	    int i = 0, i2 = 0;
	    boolean bolMask = false;
	    String retorno = "";
	    while(i < tamCampo){
	        if (i2 < sMask.length()){
		    	bolMask = ((sMask.charAt(i2) == '-') || (sMask.charAt(i2) == '.') || (sMask.charAt(i2) == '/') || (sMask.charAt(i2) == ':'));
		    	bolMask = bolMask || ((sMask.charAt(i2) == '(') || (sMask.charAt(i2) == ')') || (sMask.charAt(i2) == ' '));
		    	if (bolMask){
		    	    retorno = retorno + sMask.charAt(i2);
		    	}else{
		    	    retorno = retorno + sValue.charAt(i);
		    	    i++;
		    	}
	    	    i2++;
	        }else{
	            retorno = retorno + sValue.charAt(i);
	            i++;
	        }
	    }
	    return retorno;
	}
	public static String retiraMascara(String value){
	    int i = 0;
	    boolean bolMask = false;
	    String retorno = "";
	    while(i < value.length()){
	    	bolMask = ((value.charAt(i) == '-') || (value.charAt(i) == '.') || (value.charAt(i) == '/') || (value.charAt(i) == ':'));
	    	bolMask = bolMask || ((value.charAt(i) == '(') || (value.charAt(i) == ')') || (value.charAt(i) == ' '));
	    	if (!bolMask){
	    	    retorno = retorno + value.charAt(i);
	    	}
	    	i++;
	    }
	    return retorno;
	}
	/**
	 * Faz o calculo da idade com base na data passada como parametro.
	 * @param dDataNasc
	 * @return
	 */
	public static String calculaIdade(Date dDataNasc, String type, Date ateData){
	    if (dDataNasc == null){
	        return "";
	    }
	    
	    int anoResult = 0;
	    int mesResult = 0;
	    int diaResult = 0;
	    Date now = null;
	    if (ateData == null){
	        now = Util.getDataAtual();
	    }else{
	        now = ateData;
	    }
	    String retorno = "";
	    String strAno;
	    String strAnos;
	    String strMes;
	    String strMeses;
	    String strDia;
	    String strDias;
	    
	    int anoHoje = getYear(now);
	    int anoDataParm = getYear(dDataNasc);
	    int mesHoje = getMonth(now);
	    int mesDataParm = getMonth(dDataNasc);
	    int diaHoje = getDay(now);
	    int diaDataParm = getDay(dDataNasc);
	    
	    if ("SHORT".equalsIgnoreCase(type)){
	        strAno = strAnos = "a";
	        strMes = strMeses = "m";
	        strDia = strDias = "d";
	    }else{
	        strAno = " ano";
	        strAnos = " anos";
	        strMes = " m�s";
	        strMeses = " meses";
	        strDia = " dia";
	        strDias = " dias";
	    }
    
	    anoResult = anoHoje - anoDataParm;
	    if ((mesHoje < mesDataParm) ||
           ((mesHoje == mesDataParm) && 
            (diaHoje < diaDataParm))){
	        anoResult = anoResult - 1;
	    }
    
	    mesResult = mesHoje - mesDataParm;
	    if ((mesResult < 0) || 
	       ((mesHoje == mesDataParm) && 
	        (diaHoje < diaDataParm))){
	        mesResult = mesResult + 12;
	    }
    
	    diaResult = (diaHoje - diaDataParm);
	    if (diaResult < 0){
	    	mesResult = mesResult - 1;
	    	diaResult = 30 + diaResult;
	    }
    
	    retorno = "";
        if (anoResult > 0){
            if (anoResult == 1){
                retorno = "1" + strAno + " ";
            }else{
                retorno = String.valueOf(anoResult) + strAnos + " ";
            }
        }
    
        if (mesResult > 0){
            if (mesResult == 1){
                retorno = retorno + "1" + strMes + " ";
            }else{
                retorno = retorno + String.valueOf(mesResult) + strMeses + " ";
            }
        }
    
        if (diaResult > 0){
            if (diaResult == 1){
                retorno = retorno + "1" + strDia + " ";
            }else{
                retorno = retorno + String.valueOf(diaResult) + strDias + " ";
            }
        }
    
        return retorno;
	}
	/**
	 * Recebe e gera um string com apenas numeros nela.
	 * @param num
	 * @return
	 */
	public static String apenasNumeros(String num){
	    if (num == null) return "";
	    String aux = "";
	    for (int i = 0; i < num.length(); i++){
	        if (num.charAt(i) == '0' ||
	                num.charAt(i) == '1' ||
	                num.charAt(i) == '2' ||
	                num.charAt(i) == '3' ||
	                num.charAt(i) == '4' ||
	                num.charAt(i) == '5' ||
	                num.charAt(i) == '6' ||
	                num.charAt(i) == '7' ||
	                num.charAt(i) == '8' ||
	                num.charAt(i) == '9'){
	            aux = aux + num.charAt(i);
	        }
	    }
	    return aux;
	}
	public static String formatMoney(double value, String mask){
	    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
	    dfs.setDecimalSeparator(',');
	    dfs.setGroupingSeparator('.');
	    
	    DecimalFormat df = new DecimalFormat(mask, dfs);
	    return df.format(value);
	}
	/**
	 * Formata numero inteiro.
	 * @param numero
	 * @param formato
	 * @return
	 */
	public static String formatInt(int numero, String formato){
		String retorno;
		
		NumberFormat formatoAux = new DecimalFormat(formato);
		retorno = formatoAux.format(numero);
		return retorno;
	}
	public static String formatLong(long numero, String formato){
		String retorno;
		
		NumberFormat formatoAux = new DecimalFormat(formato);
		retorno = formatoAux.format(numero);
		return retorno;
	}
	public static String generateEspacosPlanoContas(int qtde){
	    String ret = "";
	    for(int i = 0; i <= qtde; i++){
	        ret = ret + "&nbsp;&nbsp;&nbsp;&nbsp;";
	    }
	    return ret;
	}
	public static String formataCelulaVaziaHTML(String string) {
	    if (string == null || string.trim().equals("")) {
	        return "&nbsp;";
	    }else{
	        return string;
	    }
	        
	}
	public static String limpaCaracter(String texto){
		
		String string = texto;
		char letra;
		String textoValido = "";

		 for(int i=0;i < string.length();i++){
			letra = string.charAt(i);
			if (Constantes.CARACTER_SEPARADOR.equals(String.valueOf(letra))){
				textoValido = textoValido + "";
			}else{
			 textoValido = textoValido + letra;
			}
		 }
		 return textoValido;	 
	}	
	public static String trataReferenciaImagem(String nome){
		
		String  strRetorno = "";
		String aux = "";
		StringBuffer stringB = new StringBuffer("NNNNN");
		
		nome = limpaCaracter(nome);
		
		aux = nome.substring(0);
		
		//for(int j = nome.length();j<5;j++){	
			//aux = nome.substring(0) + "_"; 
		//}
		
		for(int i=0;i<aux.length();i++){ 
			switch(aux.charAt(i)){
		    	case '1':
		    		stringB.setCharAt(0,'S');
		    		break;
		    	case '2':
		    		stringB.setCharAt(1,'S');
		    		break;
		    	case '3':
		    		stringB.setCharAt(2,'S');
		    		break;
		    	case '4':
		    		stringB.setCharAt(3,'S');
		    		break;
		    	case '5':
		    		stringB.setCharAt(4,'S');
		    		break;
			}
		}
	strRetorno = "lados" + stringB.toString() + ".gif";	
	return strRetorno;	
 }
	
	
	
	public static String trocaStrDataVaziaPorAtual(String strData){
		
		if(strData==null || strData.trim().length()==0){
			
			return Util.formatDateDDMMYYYY(new Date());
		}
		return strData;
	}
	
	
	public static String trocaStrValorVazioPorZero(String strValor){
		
		if(strValor==null || strValor.trim().length()==0){
			
			return "0";
		}
		return strValor;
	}	
	
	public static void verificaValorInexistente(Object[] array, int indice, String msgErro) throws Exception{
		
		if(array==null){
			throw new Exception();
		}

		if(array.length<=indice){
			throw new Exception();
		}
		
		
		if(array[indice]==null){
			throw new Exception();
		}
		
		if(array[indice].toString().trim().length()==0){
			throw new Exception();
		}
		
	}
	
	
	public static long subitraiDatas(int iTipo, Date dataInicial, Date dataFim){
		
		if(dataInicial == null || dataFim ==null){
			return 0;
		}
		
		long inic = dataInicial.getTime();
		long fim = dataFim.getTime();
		long total = fim - inic;
		long dia = total / 86400000L;
		if(iTipo== DIA){
			return dia;
		}else if(iTipo== MES){
			return dia/30;
		}else if(iTipo== ANO){
			return dia/365;
		}else{
			return 0;
		}
	}	
	public static Date somaDiaData(Date dataInicio, int numDias){
		Date dataFim = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicio);
		cal.add(Calendar.DATE, numDias - 1);

		return new Date(cal.getTimeInMillis());
		
	}
	public static String getStringRequestValue(HttpServletRequest request, String identificador, String valor, String tipoSel){
		String aux = (String) request.getAttribute(identificador);
		if (aux == null){ //Se getParameter for nulo, tenta pegar em getAttribute.
			aux = (String) request.getParameter(identificador);
		}
		aux = Util.nullPorString(aux);
		String aux2 = Util.nullPorString(valor);
		if (aux.equalsIgnoreCase(aux2)){
		    return " " + tipoSel + " ";
		}
		return "";
	}
	
	public static int geraHoraAgenda(int horaInicial, int intervaloAdicionar){
		String h = horaInicial + "";
		int horas = 0;
		int minutos = 0;
		if (h.length() == 3){
			horas = Integer.parseInt(h.substring(0, 1));
			minutos = Integer.parseInt(h.substring(1, 3));
		}else if (h.length() == 2){
			minutos = Integer.parseInt(h.substring(0, 2));
		}else if (h.length() == 1){
			minutos = Integer.parseInt(h.substring(0, 1));
		}else{
			horas = Integer.parseInt(h.substring(0, 2));
			minutos = Integer.parseInt(h.substring(2, 4));
		}
		minutos = minutos + intervaloAdicionar;
        if (minutos >= 60){
            int x = minutos / 60;
            horas = horas + x;
            minutos = minutos - (x * 60);
            if (minutos < 0) minutos = 0;
        }
        return (horas * 100) + minutos;
	}
	
}