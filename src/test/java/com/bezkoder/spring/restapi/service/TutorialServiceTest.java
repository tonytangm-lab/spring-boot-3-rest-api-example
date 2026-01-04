package com.bezkoder.spring.restapi.service;

import com.bezkoder.spring.restapi.model.Tutorial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TutorialServiceTest {

    private TutorialService service;

    @BeforeEach
    void setUp() {
        service = new TutorialService();
        // reset static state
        TutorialService.tutorials.clear();
        TutorialService.id = 0;
    }

    @Test
    void testSaveCreateAndFindAll() {
        Tutorial t1 = new Tutorial();
        t1.setTitle("Java");
        t1.setDescription("desc");
        t1.setPublished(true);

        Tutorial saved = service.save(t1);
        assertEquals(1L, saved.getId());
        List<Tutorial> all = service.findAll();
        assertEquals(1, all.size());
        assertSame(saved, all.get(0));
    }

    @Test
    void testSaveUpdate() {
        Tutorial t = new Tutorial();
        t.setTitle("Original");
        t.setDescription("d");
        t.setPublished(false);
        Tutorial saved = service.save(t);

        saved.setTitle("Updated");
        saved.setPublished(true);
        Tutorial updated = service.save(saved);

        assertEquals(saved.getId(), updated.getId());
        Tutorial found = service.findById(saved.getId());
        assertNotNull(found);
        assertEquals("Updated", found.getTitle());
        assertTrue(found.isPublished());
    }

    @Test
    void testFindByIdNotFound() {
        assertNull(service.findById(999L));
    }

    @Test
    void testFindByTitleContaining() {
        Tutorial t1 = new Tutorial(); t1.setTitle("Spring Boot Guide"); service.save(t1);
        Tutorial t2 = new Tutorial(); t2.setTitle("Other"); service.save(t2);

        List<Tutorial> results = service.findByTitleContaining("Boot");
        assertEquals(1, results.size());
        assertTrue(results.get(0).getTitle().contains("Boot"));
    }

    @Test
    void testFindByPublished() {
        Tutorial t1 = new Tutorial(); t1.setTitle("Pub"); t1.setPublished(true); service.save(t1);
        Tutorial t2 = new Tutorial(); t2.setTitle("Unpub"); t2.setPublished(false); service.save(t2);

        List<Tutorial> published = service.findByPublished(true);
        assertEquals(1, published.size());
        assertTrue(published.get(0).isPublished());
    }

    @Test
    void testDeleteByIdAndDeleteAll() {
        Tutorial t1 = new Tutorial(); t1.setTitle("A"); service.save(t1);
        Tutorial t2 = new Tutorial(); t2.setTitle("B"); service.save(t2);

        assertEquals(2, service.findAll().size());
        service.deleteById(1L);
        assertEquals(1, service.findAll().size());

        service.deleteAll();
        assertEquals(0, service.findAll().size());
    }
}
