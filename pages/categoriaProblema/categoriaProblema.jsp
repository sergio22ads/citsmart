<%@ taglib uri="/tags/cit" prefix="cit"%>
<%@page import="br.com.centralit.citcorpore.util.WebUtil"%>
<%@page import="br.com.centralit.citcorpore.bean.UsuarioDTO"%>
<%@page import="br.com.citframework.dto.Usuario"%>
<%@ taglib uri="/tags/i18n" prefix="i18n"%>

<!doctype html public "✰">
<html>
<head>
<%
    response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", -1);
			String iframe = "";
			iframe = request.getParameter("iframe");
%>
<%@include file="/include/security/security.jsp"%>
<html lang="en-us" class="no-js">
<!--<![endif]-->
<title><i18n:message key="citcorpore.comum.title"/></title>
<%@include file="/include/noCache/noCache.jsp"%>
<%@include file="/include/header.jsp"%>
<%@include file="/include/javaScriptsComuns/javaScriptsComuns.jsp"%>
<script>

	var objTab = null;

	addEvent(window, "load", load, false);
	function load() {
		document.form.afterRestore = function() {
			$('.tabs').tabs('select', 0);
		}
	}

	function LOOKUP_CATEGORIAPROBLEMA_select(id, desc) {
		document.form.restore({
			idCategoriaProblema : id
		});
	}
	
	function excluir() {
		if (document.getElementById("idCategoriaProblema").value != "") {
			if (confirm(i18n_message("citcorpore.comum.deleta"))) {
				document.form.fireEvent("delete");
			}
		}
	}

</script>
<%//se for chamado por iframe deixa apenas a parte de cadastro da p�gina
			if (iframe != null) {%>
<style>
div#main_container {
	margin: 10px 10px 10px 10px;
}
</style>
<%}%>
</head>
<body>
	<div id="wrapper">
		<%if (iframe == null) {%>
		<%@include file="/include/menu_vertical.jsp"%>
		<%}%>
		<div id="main_container" class="main_container container_16 clearfix">
		<%if (iframe == null) {%>
			<%@include file="/include/menu_horizontal.jsp"%>
			<%}%>

			<div class="flat_area grid_16">
				<h2>
					<i18n:message key="categoriaProblema.categoriaProblema" />
				</h2>
			</div>
			<div class="box grid_16 tabs">
				<ul class="tab_header clearfix">
					<li><a href="#tabs-1"><i18n:message key="categoriaProblema.cadastro" />
					</a></li>
					<li><a href="#tabs-2" class="round_top"><i18n:message key="categoriaProblema.pesquisa" />
					</a></li>
				</ul>
				<a href="#" class="toggle">&nbsp;</a>
				<div class="toggle_container">
					<div id="tabs-1" class="block">
						<div class="section">
							<form name='form' onsubmit='javascript:return false;'
								action='<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/pages/categoriaProblema/categoriaProblema'>
								<input type='hidden' name='idCategoriaProblema' /> 
								<input type='hidden' name='dataInicio' id="dataInicio" />
								<input type='hidden' name='dataFim' id="dataFim" />
								<div class="columns clearfix">
									<div class="col_40">
										<fieldset>
											<label class="campoObrigatorio"><i18n:message key="categoriaProblema.nome" /></label>
											<div>
												<input type='text' maxlength="100"  name="nomeCategoria" id="nomeCategoria" class="Valid[Required] Description[categoriaProblema.nome]" />
											</div>
										</fieldset>	
									</div>
									<div class="col_20">
										<fieldset>
											<label class="campoObrigatorio"><i18n:message key="categoriaProblema.nomeFluxo" /></label>
											<div>
												<select name="idTipoFluxo" id="idTipoFluxo"
													class="Valid[Required] Description[categoriaProblema.nomeFluxo]">
													</select>
											</div>
										</fieldset>
									</div>
									<div class="col_20">
										<fieldset>
											<label class="campoObrigatorio"><i18n:message key="categoriaProblema.nomeGrupoExecutor" /></label>
											<div>
												<select name="idGrupoExecutor" id="idGrupoExecutor"
													class="Valid[Required] Description[categoriaProblema.nomeGrupoExecutor]">
												</select>
											</div>
										</fieldset>
									</div>
									<div class="col_20">
										<fieldset>
											<label ><i18n:message key="categoriaProblema.templateProblema" /></label>
											<div>
												<select name="idTemplate" id="idTemplate">
												</select>
											</div>
										</fieldset>
									</div>
									<div class="col_30">
										<fieldset>
											<label class="campoObrigatorio"><i18n:message key="solicitacaoServico.impacto" /></label>
										<div>
										<select  id="impacto" name="impacto">
											<option value="B"><i18n:message key="citcorpore.comum.baixa"/></option>
											<option value="M"><i18n:message key="citcorpore.comum.media"/></option>
											<option value="A"><i18n:message key="citcorpore.comum.alta"/></option>
										</select>
										</div>
										</fieldset>
										</div>
						
									<div class="col_30" >
										<fieldset>
											<label class="campoObrigatorio"><i18n:message key="solicitacaoServico.urgencia"/></label>
											<div>
												<select  id="urgencia" name="urgencia">
													<option value="B"><i18n:message key="citcorpore.comum.baixa"/></option>
													<option value="M"><i18n:message key="citcorpore.comum.media"/></option>
													<option value="A"><i18n:message key="citcorpore.comum.alta"/></option>
												</select>
											</div>
										</fieldset>
									</div>
								</div>
								<br>
								<br>
								<button type='button' name='btnGravar' class="light"
									onclick='document.form.save();'>
									<img
										src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/pencil.png">
									<span><i18n:message key="citcorpore.comum.gravar" />
									</span>
								</button>
								<button type='button' name='btnLimpar' class="light"
									onclick='document.form.clear(); document.form.nomeCategoria.focus()'>
									<img
										src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/clear.png">
									<span><i18n:message key="citcorpore.comum.limpar" />
									</span>
								</button>
								<button type='button' name='btnExcluir' id="btnExcluir"
									class="light" onclick='excluir();'>
									<img
										src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/trashcan.png">
									<span><i18n:message key="citcorpore.comum.excluir" />
									</span>
								</button>
							</form>
						</div>
					</div>
					<div id="tabs-2" class="block">
						<div class="section">
							<i18n:message key="citcorpore.comum.pesquisa" />
							<form name='formPesquisa'>
								<cit:findField formName='formPesquisa'lockupName='LOOKUP_CATEGORIAPROBLEMA' id='LOOKUP_CATEGORIAPROBLEMA' top='0'left='0' len='550' heigth='400' javascriptCode='true' htmlCode='true' />
							</form>
						</div>
					</div>
					<!-- ## FIM - AREA DA APLICACAO ## -->
				</div>
			</div>
		</div>
		<!-- Fim da Pagina de Conteudo -->
	</div>
	<%@include file="/include/footer.jsp"%>
</body>
</html>
