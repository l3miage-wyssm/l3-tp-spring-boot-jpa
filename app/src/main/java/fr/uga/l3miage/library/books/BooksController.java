package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class BooksController {

    private final BookService bookService;
    private final BooksMapper booksMapper;

    @Autowired
    public BooksController(BookService bookService, BooksMapper booksMapper) {
       this.bookService = bookService;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/books/v1")
    public Collection<BookDTO> books(@RequestParam("q") String query) {
        Collection <Book> livre=bookService.findByTitle(query);
        return booksMapper.entityToDTO(livre);
    }

    @GetMapping("/books/{id}")
    public BookDTO book(@PathVariable("id") Long id) throws EntityNotFoundException {
        try{
        Book livre = bookService.get(id);
        return booksMapper.entityToDTO(livre);
        }
        catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping("/authors/{authorId}/books")
    public BookDTO newBook(Long authorId, BookDTO book) throws EntityNotFoundException{
        try{
            bookService.getByAuthor(authorId);
            Book livre = bookService.save(authorId, booksMapper.dtoToEntity(book));
            return booksMapper.entityToDTO(livre);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        //bookService.save(authorId,booksMapper.dtoToEntity(book));
        
    }

    
    public BookDTO updateBook(Long authorId, BookDTO book) {
        // attention BookDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        return null;
    }

    public void deleteBook(Long id) {

    }


    public void addAuthor(Long authorId, AuthorDTO author) {

    }
}
