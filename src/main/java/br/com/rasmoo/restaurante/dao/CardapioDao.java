package br.com.rasmoo.restaurante.dao;

import br.com.rasmoo.restaurante.entity.Cardapio;
import br.com.rasmoo.restaurante.entity.Categoria;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CardapioDao {

  private EntityManager entityManager;

  public CardapioDao(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void cadastrar(final Cardapio cardapio) {
    this.entityManager.persist(cardapio);
  }

  public Cardapio consultarPorId(final Integer id) {
    return this.entityManager.find(Cardapio.class, id);
  }

  public List<Cardapio> consultarTodos() {
    String query = "SELECT c FROM Cardapio c";
    return this.entityManager.createQuery(query, Cardapio.class).getResultList();
  }

  public List<Cardapio> consultarPorValor(final BigDecimal valor) {
    String query = "SELECT c FROM Cardapio c WHERE c.valor = :valorCardapio";
    return this.entityManager
        .createQuery(query, Cardapio.class)
        .setParameter("valorCardapio", valor)
        .getResultList();
  }

  public List<Cardapio> consultarPorCategoria(final Categoria categoria) {
    String query = "SELECT c FROM Cardapio c WHERE c.categoria = :categoriaCardapio";
    return this.entityManager
        .createQuery(query, Cardapio.class)
        .setParameter("categoriaCardapio", categoria)
        .getResultList();
  }

  public List<Cardapio> consultarPorNomeCategoria(final String nomeCategoria) {
    String query = "SELECT c FROM Cardapio c JOIN Categoria ca ON c.categoria = ca WHERE upper(ca.nome) = upper(:nomeCategoria)";
    return this.entityManager
        .createQuery(query, Cardapio.class)
        .setParameter("nomeCategoria", nomeCategoria)
        .getResultList();
  }

  public Optional<Cardapio> consultarPorNome(final String nome) {
    try {
      String query = "SELECT c FROM Cardapio c WHERE upper(c.nome) = upper(:nomeCardapio)";
      return Optional.ofNullable(this.entityManager
          .createQuery(query, Cardapio.class)
          .setParameter("nomeCardapio", nome)
          .getSingleResult());
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  public void atualizar(final Cardapio cardapio) {
    this.entityManager.merge(cardapio);
  }

  public void excluir(final Cardapio cardapio) {
    this.entityManager.remove(cardapio);
  }

}
