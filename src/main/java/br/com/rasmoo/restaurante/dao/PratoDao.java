package br.com.rasmoo.restaurante.dao;

import br.com.rasmoo.restaurante.entity.Prato;

import javax.persistence.EntityManager;

public class PratoDao {

  EntityManager entityManager;

  public PratoDao(EntityManager entityManager){
    this.entityManager = entityManager;
  }

  public void cadastrar(final Prato prato){
    this.entityManager.persist(prato);
    System.out.println("Entidade cadastrada: "+ prato);
  }


}
