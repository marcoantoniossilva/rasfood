package br.com.rasmoo.restaurante.dao;

import br.com.rasmoo.restaurante.entity.Categoria;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class CategoriaDao {

  private EntityManager entityManager;

  public CategoriaDao(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void cadastrar(final Categoria categoria) {
    this.entityManager.persist(categoria);
  }

  public Optional<Categoria> consultar(final Integer id) {
    try {
      return Optional.ofNullable(this.entityManager.find(Categoria.class, id));
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  public List<Categoria> consultarTodos() {
    String query = "SELECT c FROM Categoria c";
    return this.entityManager.createQuery(query, Categoria.class).getResultList();
  }

  public void atualizar(final Categoria categoria) {
    this.entityManager.merge(categoria);
  }

  public void excluir(final Categoria categoria) {
    this.entityManager.remove(categoria);
  }
}
