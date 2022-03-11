package br.com.rasmoo.restaurante.service.test;

import br.com.rasmoo.restaurante.dao.CardapioDao;
import br.com.rasmoo.restaurante.dao.CategoriaDao;
import br.com.rasmoo.restaurante.entity.Cardapio;
import br.com.rasmoo.restaurante.entity.Categoria;
import br.com.rasmoo.restaurante.util.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

 // O método testarEntityManager possui várias explicações sobre estados das entidades

public class CardapioService {
  public static void main(String[] args) {
    //Interface da Jpa que é responsável por manipular as entidades
    EntityManager entityManager = JPAUtil.getEntityManagerRasfood();

    cadastrarProdutoCardapio(entityManager, cadastrarCategoria(entityManager));
  }

  private static Categoria cadastrarCategoria(EntityManager entityManager) {
    CategoriaDao categoriaDao = new CategoriaDao(entityManager);
    Categoria pratoPrincipal = new Categoria("Prato principal");

    entityManager.getTransaction().begin();
    categoriaDao.cadastrar(pratoPrincipal);

    entityManager.getTransaction().commit();
    entityManager.clear();

    return pratoPrincipal;
  }

  private static void cadastrarProdutoCardapio(EntityManager entityManager, Categoria categoria) {

    Cardapio risoto = new Cardapio();
    risoto.setNome("Risoto de frutos do mar");
    risoto.setDescricao("Risoto acompanhado de lula, polvo e mariscos");
    risoto.setDisponivel(true);
    risoto.setCategoria(categoria);
    risoto.setValor(BigDecimal.valueOf(88.5));

    Cardapio salmao = new Cardapio();
    salmao.setNome("Salmao ao molho de maracujá");
    salmao.setDescricao("Salmao grelhado ao molhode maracujá");
    salmao.setDisponivel(true);
    salmao.setCategoria(categoria);
    salmao.setValor(BigDecimal.valueOf(60.00));

    CardapioDao cardapioDao = new CardapioDao(entityManager);
    entityManager.getTransaction().begin();

    cardapioDao.cadastrar(risoto);
    entityManager.flush();
    cardapioDao.cadastrar(salmao);
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(risoto.getId()));
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(salmao.getId()));

    entityManager.close();
  }

  public void testarEntityManager(EntityManager entityManager, Categoria categoria){
    Cardapio risoto = new Cardapio();
    risoto.setNome("Risoto de frutos do mar");
    risoto.setDescricao("Risoto acompanhado de lula, polvo e mariscos");
    risoto.setDisponivel(true);
    risoto.setCategoria(categoria);
    risoto.setValor(BigDecimal.valueOf(88.5));
    //Jpa ainda não gerencia a entidade, por tanto, seu estado neste momento é TRANSIENT.

    Cardapio salmao = new Cardapio();
    salmao.setNome("Salmao ao molho de maracujá");
    salmao.setDescricao("Salmao grelhado ao molhode maracujá");
    salmao.setDisponivel(true);
    salmao.setCategoria(categoria);
    salmao.setValor(BigDecimal.valueOf(60.00));


    CardapioDao cardapioDao = new CardapioDao(entityManager);
    entityManager.getTransaction().begin();
    cardapioDao.cadastrar(risoto);
    entityManager.flush();
    /*
     * entityManager.flush() Atualiza o banco de dados conforme os procedimentos feitos, poderia usar
     * entityManager.getTransaction().commit(), mas não daria pra retornar seu estado para MANAGED futuramente.
     * Ao usar cardapioDao.cadastrar(risoto), o Jpa passa a gerenciar a entidade e um ID é criado, pois o método
     * cadastrar faz um commit na transação e ela passa a ser MANAGED.
     * Quando uma entidade está MANAGED, qualquer mudança em seu estado (como uma chamada de setter) resultará em uma
     * atualização no banco de dados no momento do commit.
     */


    cardapioDao.cadastrar(salmao); //Cadastrando novo prato
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(salmao.getId())); // Testando consulta
    salmao.setDisponivel(false);
    /* Como o estado da entidade é MANAGED, um simples set gera um update no BD no momento
     * do entityManager.getTransaction().commit(), se estiver usando entityManager.clear() para atualizar
     * é preciso usar o entityManager.flush();
     */
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(salmao.getId())); // Testando alteração de parâmetro
    cardapioDao.excluir(salmao); // Excluindo
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(salmao.getId()));// Testando exclusao


    entityManager.clear();
    /* Entidade passou pro estado de DETACHED, uma instância DETACHED (desanexada) é um objeto que foi persistido, mas
     * sua Session foi fechada. A referência ao objeto continua válida, é claro, e a instância desanexada pode ser
     * acoplada a uma nova Session no futuro, tornando-o novamente persistente, se usase entityManager.close(), não
     * daria pra retornar para MANAGED mais.
     */

    salmao.setValor(BigDecimal.valueOf(100.00));
    cardapioDao.atualizar(salmao); // Entidade voltou pro estado de MANAGED
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultar(salmao.getId()));// Testando alteração depois de entityManager.clear();

    entityManager.close(); // Entidade passou pro estado de DETACHED, sem volta.
  }
}
