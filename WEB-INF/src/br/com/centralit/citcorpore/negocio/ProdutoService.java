package br.com.centralit.citcorpore.negocio;
import java.util.Collection;

import br.com.centralit.citcorpore.bean.ProdutoDTO;
import br.com.citframework.service.CrudServiceEjb2;
public interface ProdutoService extends CrudServiceEjb2 {
	public Collection findByIdTipoProduto(Integer parm) throws Exception;
    public Collection findByIdCategoria(Integer parm) throws Exception;
    public Collection findByIdCategoriaAndAceitaRequisicao(Integer idCategoria, String aceitaRequisicao) throws Exception;
	public void deleteByIdTipoProduto(Integer parm) throws Exception;
    public void recuperaImagem(ProdutoDTO produtoDto) throws Exception;
    public Collection validaNovoProduto(ProdutoDTO produtoDto) throws Exception;
}
