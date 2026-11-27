package com.example.alertatempranagalpones

import com.example.alertatempranagalpones.data.model.EstadoGalpon
import org.junit.Assert.assertEquals
import org.junit.Test

class EstadoGalponTest {

    @Test
    fun temperaturaBajaEsNormal() {
        assertEquals(EstadoGalpon.NORMAL, EstadoGalpon.desdeTemperatura(24.5))
    }

    @Test
    fun justoBajoElUmbralDeAdvertenciaEsNormal() {
        assertEquals(EstadoGalpon.NORMAL, EstadoGalpon.desdeTemperatura(27.9))
    }

    @Test
    fun enElUmbralDeAdvertenciaEsAdvertencia() {
        assertEquals(EstadoGalpon.ADVERTENCIA, EstadoGalpon.desdeTemperatura(28.0))
    }

    @Test
    fun justoBajoElUmbralCriticoEsAdvertencia() {
        assertEquals(EstadoGalpon.ADVERTENCIA, EstadoGalpon.desdeTemperatura(31.9))
    }

    @Test
    fun enElUmbralCriticoEsCritico() {
        assertEquals(EstadoGalpon.CRITICO, EstadoGalpon.desdeTemperatura(32.0))
    }

    @Test
    fun temperaturaMuyAltaEsCritico() {
        assertEquals(EstadoGalpon.CRITICO, EstadoGalpon.desdeTemperatura(45.0))
    }

    @Test
    fun lasEtiquetasSonLasEsperadas() {
        assertEquals("Normal", EstadoGalpon.NORMAL.etiqueta)
        assertEquals("Advertencia", EstadoGalpon.ADVERTENCIA.etiqueta)
        assertEquals("Crítico", EstadoGalpon.CRITICO.etiqueta)
    }
}