package co.com.proptech.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PageRequest Unit Tests")
class PageRequestTest {

    @Test
    @DisplayName("Should accept valid page and size as-is")
    void shouldAcceptValidValues() {
        PageRequest req = new PageRequest(2, 15, "price", PageRequest.SortDirection.ASC);
        assertEquals(2, req.getPage());
        assertEquals(15, req.getSize());
        assertEquals("price", req.getSortBy());
        assertEquals(PageRequest.SortDirection.ASC, req.getDirection());
    }

    @Test
    @DisplayName("Should clamp negative page to 0")
    void shouldClampNegativePageToZero() {
        PageRequest req = new PageRequest(-5, 10, "createdAt", PageRequest.SortDirection.DESC);
        assertEquals(0, req.getPage());
    }

    @Test
    @DisplayName("Should clamp size of 0 to minimum 1")
    void shouldClampSizeZeroToOne() {
        PageRequest req = new PageRequest(0, 0, "createdAt", PageRequest.SortDirection.DESC);
        assertEquals(1, req.getSize());
    }

    @Test
    @DisplayName("Should clamp size above 1000 to maximum 1000")
    void shouldClampSizeAboveMaxToOneThousand() {
        PageRequest req = new PageRequest(0, 1001, "createdAt", PageRequest.SortDirection.DESC);
        assertEquals(1000, req.getSize());
    }

    @Test
    @DisplayName("Should accept size exactly 1000")
    void shouldAcceptSizeExactlyOneThousand() {
        PageRequest req = new PageRequest(0, 1000, "createdAt", PageRequest.SortDirection.DESC);
        assertEquals(1000, req.getSize());
    }

    @Test
    @DisplayName("Should default direction to DESC when null is passed")
    void shouldDefaultDirectionToDescWhenNull() {
        PageRequest req = new PageRequest(0, 10, "createdAt", null);
        assertEquals(PageRequest.SortDirection.DESC, req.getDirection());
    }

    @Test
    @DisplayName("Two-arg constructor should default to createdAt DESC")
    void twoArgConstructorShouldUseDefaults() {
        PageRequest req = new PageRequest(1, 20);
        assertEquals(1, req.getPage());
        assertEquals(20, req.getSize());
        assertEquals("createdAt", req.getSortBy());
        assertEquals(PageRequest.SortDirection.DESC, req.getDirection());
    }
}
