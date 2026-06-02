package ms.example.ms_proyectos.mapper;

import ms.example.ms_proyectos.dto.ProyectoRequest;
import ms.example.ms_proyectos.dto.ProyectoResponse;
import ms.example.ms_proyectos.model.Proyecto;

import java.util.Objects;

public class ProyectoMapper {

    public static Proyecto toEntity(ProyectoRequest req) {
        if (req == null) return null;
        Proyecto p = new Proyecto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setEstado(req.getEstado());
        p.setFechaInicio(req.getFechaInicio());
        p.setFechaFin(req.getFechaFin());
        return p;
    }

    public static void updateEntityFromRequest(ProyectoRequest req, Proyecto p) {
        if (req == null || p == null) return;
        if (!Objects.equals(p.getNombre(), req.getNombre())) p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setEstado(req.getEstado());
        p.setFechaInicio(req.getFechaInicio());
        p.setFechaFin(req.getFechaFin());
    }

    public static ProyectoResponse toResponse(Proyecto p) {
        if (p == null) return null;
        ProyectoResponse r = new ProyectoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        r.setEstado(p.getEstado());
        r.setFechaInicio(p.getFechaInicio());
        r.setFechaFin(p.getFechaFin());
        r.setFechaCreacion(p.getFechaCreacion());
        r.setFechaActualizacion(p.getFechaActualizacion());
        return r;
    }
}
