<%@ taglib uri="/tags/cit" prefix="cit"%>
<%@ taglib uri="/tags/i18n" prefix="i18n"%>
<%@page import="br.com.centralit.citcorpore.util.WebUtil"%>
<%@page import="br.com.centralit.citcorpore.bean.UsuarioDTO"%>
<%@page import="br.com.centralit.citcorpore.bean.InformacaoItemConfiguracaoDTO"%>
<html>
<head>
<%@include file="/include/security/security.jsp"%>
<title><i18n:message key="citcorpore.comum.title" /></title>
<%@include file="/include/noCache/noCache.jsp"%>
<%@include file="/include/javaScriptsComuns/javaScriptsComuns.jsp"%>
<% 
request.setCharacterEncoding("UTF-8");
response.setHeader("Content-Language","lang");
%>
<link rel="stylesheet" type="text/css" href="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/css/informacao.css">
<link rel="stylesheet" type="text/css" href="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/js/jqueryTreeview/jquery.treeview.css">
<script type="text/javascript" src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/js/jqueryTreeview/jquery.js"></script>
<script type="text/javascript" src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/js/jqueryTreeview/jquery.treeview.js"></script>
<script type="text/javascript" src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/js/jqueryTreeview/jquery.cookie.js"></script>

<script>
	function informa() {
		document.form.fireEvent("informacao");
	}
	
	function tree(id) {
		$(id).treeview();
		$('#loading_overlay').show();
	}	

	function restaurarValoresBios() {
		document.form.fireEvent("prepararHtmlBios");
		$('#loading_overlay').show();
	}

	function restaurarValoresHardware() {
		document.form.fireEvent("prepararHtmlHardware");
		$('#loading_overlay').show();
	}

	function restaurarValoresSoftware() {
		document.form.fireEvent("prepararHtmlSoftware");
		$('#loading_overlay').show();
	}
</script>

</head>
	<body id="bodyInf">	
		<form name='form' action='<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/pages/informacaoItemConfiguracao/informacaoItemConfiguracao'>
			<input type='hidden' name='idItemConfiguracao'/> 
			<div id="principalInf" style="width: 90%;">
			</div>
		</form>
		<div id="loading_overlay">
			<div class="loading_message round_bottom">
				<img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/loading.gif" alt="aguarde..." />
			</div>
	    </div>
	</body>
</html>