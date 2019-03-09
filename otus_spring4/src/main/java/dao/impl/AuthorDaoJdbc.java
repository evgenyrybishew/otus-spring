package dao.impl;


import dao.helper.SqlHelper;
import dao.interfaces.AuthorDao;
import domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import rowmapper.AuthorMapper;

import java.util.List;

import static dao.helper.SqlHelper.*;
import static rowmapper.MapperConstant.*;

@Repository
public class AuthorDaoJdbc implements AuthorDao {

    private JdbcOperations jdbcOperations;
    private static String TABLE_NAME = "author";

    @Autowired
    public AuthorDaoJdbc(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public List<Author> getByName(String firstname) {
        return query(SELECT + TABLE_NAME + WHERE + FIRST_NAME + CONDITION, new Object[]{firstname});
    }

    public List<Author> getByLastName(String lastname) {
        return query(SELECT + TABLE_NAME + WHERE + LAST_NAME + CONDITION, new Object[]{lastname});
    }

    public Author getById(long id) {
        return jdbcOperations.queryForObject(SELECT + TABLE_NAME + WHERE + ID + CONDITION, new Object[]{id}, new AuthorMapper());
    }

    public long count() {
        return SqlHelper.count(jdbcOperations, TABLE_NAME);
    }

    public void insert(Author author) {
        String sql = String.format("insert in to %s (%s, %s) values (?, ?)", TABLE_NAME, FIRST_NAME, LAST_NAME);
        jdbcOperations.update(sql, author.getFirstname(), author.getFirstname());
    }


    private List<Author> query(String sql, Object[] parameters) {
        return jdbcOperations.query(sql, parameters, new AuthorMapper());
    }

    public void createTable() {
        createAuthorTable(jdbcOperations);
    }
}
