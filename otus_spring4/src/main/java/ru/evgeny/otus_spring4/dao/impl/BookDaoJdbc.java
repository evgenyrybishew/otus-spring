package ru.evgeny.otus_spring4.dao.impl;

import ru.evgeny.otus_spring4.dao.helper.SqlHelper;
import ru.evgeny.otus_spring4.dao.interfaces.AuthorDao;
import ru.evgeny.otus_spring4.dao.interfaces.BookDao;
import ru.evgeny.otus_spring4.dao.interfaces.GenreDao;
import ru.evgeny.otus_spring4.dao.interfaces.PublisherDao;
import ru.evgeny.otus_spring4.domain.Author;
import ru.evgeny.otus_spring4.domain.Book;
import ru.evgeny.otus_spring4.domain.Publisher;
import org.h2.table.SubQueryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.evgeny.otus_spring4.rowmapper.BookMapper;

import java.util.List;

import static ru.evgeny.otus_spring4.dao.helper.SqlHelper.*;
import static ru.evgeny.otus_spring4.rowmapper.MapperConstant.*;

@Repository
public class BookDaoJdbc implements BookDao {

    private static String TABLE_NAME = "book";

    private JdbcOperations jdbcOperations;
    private AuthorDao authorDao;
    private GenreDao genreDao;
    private PublisherDao publisherDao;

    @Autowired
    public BookDaoJdbc(JdbcOperations jdbcOperations, AuthorDao authorDao, GenreDao genreDao, PublisherDao publisherDao) {
        this.jdbcOperations = jdbcOperations;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
        this.publisherDao = publisherDao;
    }

    public long count() {
        return SqlHelper.count(jdbcOperations, TABLE_NAME);
    }

    public Book getById(long id) {
        return jdbcOperations
                .queryForObject(SELECT + TABLE_NAME + WHERE + ID + CONDITION, new Object[]{id},
                        new BookMapper(authorDao, genreDao, publisherDao));
    }

    public List<Book> getByAuthor(Author author) {
        return query(SELECT + TABLE_NAME + WHERE + AUTHOR + CONDITION, new Object[]{author.getId()});
    }

    public List<Book> getByName(String name) {
        return query(SELECT + TABLE_NAME + WHERE + NAME + CONDITION, new Object[]{name});
    }

    public List<Book> getByPublisher(Publisher publisher) {
        return query(SELECT + TABLE_NAME + WHERE + PUBLISHER + CONDITION, new Object[]{publisher.getId()});
    }

    public List<Book> getAll() {
        return jdbcOperations.queryForList(SELECT + TABLE_NAME, Book.class, new BookMapper(authorDao, genreDao, publisherDao));
    }

    public void insert(Book book) {
        String sql = String.format("insert into %s (%s, %s, %s, %s) values (?, ?, ?, ?)", TABLE_NAME, NAME, AUTHOR, GENRE, PUBLISHER);
        jdbcOperations.update(sql, book.getName(), book.getAuthor().getId(), book.getGenre().getId(), book.getPublisher().getId());
    }

    private List<Book> query(String sql, Object[] parameters) {
        return jdbcOperations.query(sql, parameters, new BookMapper(authorDao, genreDao, publisherDao));
    }

    public void createTable() {
        createBookTable(jdbcOperations);
    }

    @Override
    public void dropTable() {
        SqlHelper.dropTable(jdbcOperations, TABLE_NAME);
    }
}
