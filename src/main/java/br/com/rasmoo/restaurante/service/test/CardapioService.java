package br.com.rasmoo.restaurante.service.test;

import br.com.rasmoo.restaurante.dao.CardapioDao;
import br.com.rasmoo.restaurante.dao.CategoriaDao;
import br.com.rasmoo.restaurante.entity.Cardapio;
import br.com.rasmoo.restaurante.entity.Categoria;
import br.com.rasmoo.restaurante.util.CargaDeDadosUtil;
import br.com.rasmoo.restaurante.util.JPAUtil;
import org.h2.util.json.JsonConstructorUtils;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

// O método testarEntityManager possui várias explicações sobre estados das entidades

public class CardapioService {
  public static void main(String[] args) {
    EntityManager entityManager = JPAUtil.getEntityManagerRasFood();
    entityManager.getTransaction().begin();
    CargaDeDadosUtil.cadastarCategorias(entityManager);
    CargaDeDadosUtil.cadastrarProdutosCardapio(entityManager);
    CardapioDao cardapioDao = new CardapioDao(entityManager);
    CategoriaDao categoriaDao = new CategoriaDao(entityManager);

    System.out.println("Lista de produtos:");
    cardapioDao.consultarTodos().forEach(System.out::println);

    System.out.println("Lista de produtos que custam R$59,50:");
    cardapioDao.consultarPorValor(BigDecimal.valueOf(59.50)).forEach(System.out::println);

    System.out.println("Lista de produtos da categoria 1:");
    categoriaDao.consultar(1)
        .ifPresentOrElse((categoria) -> {
          cardapioDao.consultarPorCategoria(categoria)
              .forEach(System.out::println);
        }, () -> System.out.println("Nada encontrado!"));

    System.out.println("Lista de produtos da categoria \"Saladas\":");
    cardapioDao.consultarPorNomeCategoria("Saladas").forEach(System.out::println);

    System.out.println("Produto pesquisado: \"Cordeiro\":");
    cardapioDao.consultarPorNome("Cordeiro").ifPresentOrElse(System.out::println, () -> System.out.println("Nada encontrado!"));
    entityManager.close();
  }

  public static void testarEntityManager(EntityManager entityManager, Categoria categoria) {
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
    CategoriaDao categoriaDao = new CategoriaDao(entityManager);
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
    System.out.println("O prato consultado foi: " + cardapioDao.consultarPorId(salmao.getId())); // Testando consulta
    salmao.setDisponivel(false);
    /* Como o estado da entidade é MANAGED, um simples set gera um update no BD no momento
     * do entityManager.getTransaction().commit(), se estiver usando entityManager.clear() para atualizar
     * é preciso usar o entityManager.flush();
     */
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultarPorId(salmao.getId())); // Testando alteração de parâmetro
    cardapioDao.excluir(salmao); // Excluindo
    System.out.println("O prato consultado foi: " + cardapioDao.consultarPorId(salmao.getId()));// Testando exclusao


    entityManager.clear();
    /* Entidade passou pro estado de DETACHED, uma instância DETACHED (desanexada) é um objeto que foi persistido, mas
     * sua Session foi fechada. A referência ao objeto continua válida, é claro, e a instância desanexada pode ser
     * acoplada a uma nova Session no futuro, tornando-o novamente persistente, se usase entityManager.close(), não
     * daria pra retornar para MANAGED mais.
     */

    salmao.setValor(BigDecimal.valueOf(100.00));
    cardapioDao.atualizar(salmao); // Entidade voltou pro estado de MANAGED
    entityManager.flush();
    System.out.println("O prato consultado foi: " + cardapioDao.consultarPorId(salmao.getId()));// Testando alteração depois de entityManager.clear();

    System.out.println("Lista de produtos:");
    cardapioDao.consultarTodos().forEach(System.out::println);

    System.out.println("Lista de produtos que custam R$59,00:");
    cardapioDao.consultarPorValor(BigDecimal.valueOf(59.00)).forEach(System.out::println);

    System.out.println("Lista de produtos da categoria 1:");
    categoriaDao.consultar(1)
        .ifPresentOrElse((cat) -> {
          cardapioDao.consultarPorCategoria(cat)
              .forEach(System.out::println);
        }, () -> System.out.println("Nada encontrado!"));

    System.out.println("Lista de produtos po da categoria \"Saladas\":");
    cardapioDao.consultarPorNomeCategoria("Saladas").forEach(System.out::println);

    System.out.println("Produto pesquisado: \"Cordeiro\":");
    cardapioDao.consultarPorNome("Cordeiro").ifPresentOrElse(System.out::println, () -> System.out.println("Nada encontrado!"));

    entityManager.close(); // Entidade passou pro estado de DETACHED, sem volta.
  }
}
