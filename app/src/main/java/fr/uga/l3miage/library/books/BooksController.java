package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.data.domain.Book.Language;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
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

    @GetMapping("/v1/books")
    public Collection<BookDTO> books(@RequestParam(required = false) String query) {
        Collection <Book> livres;
        if (query==null){
            livres=bookService.list();
        }
        else {
            livres=bookService.findByTitle(query);
        }
        return livres.stream()
            .map(booksMapper::entityToDTO)
            .toList();
    }

    @GetMapping("/v1/books/{id}")
    public BookDTO book(@PathVariable("id") Long id) throws EntityNotFoundException {
        try{
        Book livre = bookService.get(id);
        return booksMapper.entityToDTO(livre);
        }
        catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping("/v1/authors/{authorId}/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO newBook(@PathVariable("authorId") Long authorId,@RequestBody BookDTO book) throws EntityNotFoundException{
        Book livre;
        Language[] langages = Book.Language.values();
        try{
            bookService.getByAuthor(authorId);
            if (book.title()==null || book.title().trim()==""){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Pas de titre !");
            }  
            if(book.language()!=null){
                if(!(book.language().replaceAll("\\s", " ").equals(langages[1].name().toLowerCase()) || book.language().replaceAll("\\s", " ").equals(langages[0].name().toLowerCase()))){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Language incorrecte");
                }
            }
            
            if (book.isbn()<1000000000 || book.isbn()>100000000000000L){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ISBN incorrecte");
            }
            if (book.year()<200 || book.year()>Year.now().getValue()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Année incorrecte");
            }
            livre = bookService.save(authorId, booksMapper.dtoToEntity(book));
            return booksMapper.entityToDTO(livre);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        //bookService.save(authorId,booksMapper.dtoToEntity(book));
        
    }
    

    
    @PutMapping("/v1/books/{authorId}")
    public BookDTO updateBook(@PathVariable("authorId") Long authorId,@RequestBody BookDTO book) throws EntityNotFoundException{
        // attention BookDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        if(authorId==book.id()){
            bookService.update(booksMapper.dtoToEntity(book));
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return book;
    }

    @DeleteMapping("/v1/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        try{
            bookService.delete(id);
        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }  

    }


    //@PutMapping("/V1/books/{id}/author")
    public void addAuthor(Long authorId, AuthorDTO author) {

    }
}
