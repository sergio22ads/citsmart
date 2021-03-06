package br.com.centralit.citcorpore.rh.ajaxForms;
 
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.centralit.citajax.html.AjaxFormAction;
import br.com.centralit.citajax.html.DocumentHTML;
import br.com.centralit.citajax.html.HTMLForm;
import br.com.centralit.citajax.util.Constantes;
import br.com.centralit.citcorpore.bean.UsuarioDTO;
import br.com.centralit.citcorpore.rh.bean.CertificacaoDTO;
import br.com.centralit.citcorpore.rh.negocio.CertificacaoService;
import br.com.centralit.citcorpore.util.UtilI18N;
import br.com.centralit.citcorpore.util.WebUtil;
import br.com.citframework.service.ServiceLocator;
 
 
public class Certificacao extends AjaxFormAction {
 
      public Class getBeanClass() {
            return CertificacaoDTO.class;
      }
      
      private boolean validaSessao(DocumentHTML document, HttpServletRequest request) {

    	  UsuarioDTO usuario = WebUtil.getUsuario(request);
    	  
          if (usuario == null) {

                document.alert(UtilI18N.internacionaliza(request, "citcorpore.comum.sessaoExpirada"));
                document.executeScript("window.location = '" + Constantes.getValue("SERVER_ADDRESS") + request.getContextPath() + "'");
                
                return false;
          }

    	  return true;
      }
 
      public void load(DocumentHTML document, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	  
    	  if (!this.validaSessao(document, request)) {
    		  
    		  return;
    	  }
    	  
          CertificacaoDTO certificacoesDto = new CertificacaoDTO();
        
          HTMLForm form = document.getForm("form");
          form.setValues(certificacoesDto);
      }
              
      public void save(DocumentHTML document, HttpServletRequest request, HttpServletResponse response) throws Exception {

    	  if (!this.validaSessao(document, request)) {
    		  
    		  return;
    	  }
          
          CertificacaoDTO certificacoesDto  = (CertificacaoDTO) document.getBean();
          CertificacaoService certificacoesService = (CertificacaoService) ServiceLocator.getInstance().getService(CertificacaoService.class, null);
          
          if (certificacoesDto.getIdCertificacao() != null){
        	  
              certificacoesService.update(certificacoesDto);
              document.alert(UtilI18N.internacionaliza(request, "MSG06"));
          }else{
        	  
              certificacoesService.create(certificacoesDto);
              document.alert(UtilI18N.internacionaliza(request, "MSG05"));
          }
          
          document.executeScript("limpar()");
      }
      
      public void delete(DocumentHTML document, HttpServletRequest request, HttpServletResponse response) throws Exception {

    	  if (!this.validaSessao(document, request)) {
    		  
    		  return;
    	  }   	  
          
          CertificacaoDTO certificacoesDto = (CertificacaoDTO) document.getBean();
          
          CertificacaoService certificacoesService = (CertificacaoService) ServiceLocator.getInstance().getService(CertificacaoService.class, null);
          certificacoesDto = (CertificacaoDTO) certificacoesService.restore(certificacoesDto);
          
          certificacoesService.delete(certificacoesDto);
          document.executeScript("limpar()");
          
          document.alert(UtilI18N.internacionaliza(request, "MSG07")); 
      }
      
      public void restore(DocumentHTML document, HttpServletRequest request, HttpServletResponse response) throws Exception {

    	  if (!this.validaSessao(document, request)) {
    		  
    		  return;
    	  }
    	  
          CertificacaoDTO certificacoesDto = (CertificacaoDTO) document.getBean();
          if (certificacoesDto.getIdCertificacao() == null)
                return;
          
          CertificacaoService certificacoesService = (CertificacaoService) ServiceLocator.getInstance().getService(CertificacaoService.class, null);
          certificacoesDto = (CertificacaoDTO) certificacoesService.restore(certificacoesDto);
 
          HTMLForm form = document.getForm("form");
          form.setValues(certificacoesDto);
    }
      
      public void exibeSelecao(DocumentHTML document, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	  CertificacaoService certificacoesService = (CertificacaoService) ServiceLocator.getInstance().getService(CertificacaoService.class, null);
          Collection<CertificacaoDTO> col = certificacoesService.list();
          if (col != null) {
          	for (CertificacaoDTO certificacaoDto : col) {
          		document.executeScript("incluirOpcaoSelecao(\""+certificacaoDto.getIdCertificacao()+"\",\""+certificacaoDto.getDescricao().trim()+"\",\""+certificacaoDto.getDetalhe().trim()+"\");");
  			}
          }
      }
      
}


