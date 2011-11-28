package resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import model.Facility;
import model.Image;
import model.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import service.FacilityService;
import service.ImageService;
import service.RoomTypeService;
import service.StructureService;

@Path("/roomTypes/")
@Component
@Scope("prototype")
public class RoomTypeResource {
	
	@Autowired
	private RoomTypeService roomTypeService = null;
	@Autowired
    private StructureService structureService = null;
	@Autowired
	private FacilityService facilityService = null;
	@Autowired
	private ImageService imageService = null;
	
	
	@GET
	@Path("structure/{idStructure}/{offset}/{rownum}")
	@Produces({MediaType.APPLICATION_JSON})
	public List<RoomType> getRoomTypes(@PathParam("idStructure") Integer idStructure,@PathParam("offset") Integer offset,@PathParam("rownum") Integer rownum){
		List<RoomType> ret = null;
		List<Image> images = null;
		List<Facility> facilities = null;
		
		ret = this.getRoomTypeService().findRoomTypesByIdStructure(idStructure,offset,rownum);
		for(RoomType each: ret){
			images = this.getImageService().findImagesByIdRoomType(each.getId());
			each.setImages(images);
			facilities = this.getFacilityService().findRoomTypeFacilitiesByIdRoomType(each.getId());
			each.setFacilities(facilities);
		}
		return ret;
	}
	
	@GET
    @Path("structure/{idStructure}/search")
    @Produces({MediaType.APPLICATION_JSON})
	public List<RoomType> simpleSearch(@PathParam("idStructure") Integer idStructure, @QueryParam("term") String term){
		List<RoomType> ret = null;
		List<Image> images = null;
		List<Facility> facilities = null;
		
		ret = new ArrayList<RoomType>();
		for(RoomType each: this.getRoomTypeService().findRoomTypesByIdStructure(idStructure)){
			if(this.simpleSearchFilter(each, term)){
				images = this.getImageService().findImagesByIdRoomType(each.getId());
				each.setImages(images);
				facilities = this.getFacilityService().findRoomTypeFacilitiesByIdRoomType(each.getId());
				each.setFacilities(facilities);
				ret.add(each);
			}
		}
		return ret;
	}
	
	public boolean simpleSearchFilter(RoomType roomType, String term){
		boolean ret = false;
		
		ret = (roomType.getName() != null && roomType.getName().contains(term)) || 
			(roomType.getNotes() != null && roomType.getNotes().contains(term) )|| 
			(roomType.getMaxGuests() != null && roomType.getMaxGuests().toString().contains(term));
		return ret;
	}
	
	@POST
    @Path("structure/{idStructure}/advancedSearch")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public List<RoomType> advancedSearch(@PathParam("idStructure") Integer idStructure,RoomType example){
        List<RoomType> filteredRoomTypes = null;
       
        filteredRoomTypes = new ArrayList<RoomType>();
        for(RoomType each: this.getRoomTypeService().findRoomTypesByIdStructure(idStructure)){             
            	if(this.advancedSearchFilter(each, example)){
            		filteredRoomTypes.add(each);            		
            	}                       
        }       
        return filteredRoomTypes;          
    }
	
	private boolean advancedSearchFilter(RoomType each, RoomType example){
    	boolean ret = false;
    	
    	ret = each.getName().contains(example.getName()) || each.getMaxGuests().equals(example.getMaxGuests()) || each.getNotes().contains(example.getNotes());
    	return ret;
    } 
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public RoomType getRoomType(@PathParam("id") Integer id){
		RoomType ret = null;
		List<Image> images = null;
		List<Facility> facilities = null;
		
		ret = this.getRoomTypeService().findRoomTypeById(id);
		images = this.getImageService().findImagesByIdRoomType(id);
		ret.setImages(images);
		facilities = this.getFacilityService().findRoomTypeFacilitiesByIdRoomType(id);
		ret.setFacilities(facilities);
		return ret;
	}
	
	@POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public RoomType save(RoomType roomType) {
       
        this.getRoomTypeService().insertRoomType(roomType);
        System.out.println("id:"+roomType.getId());
        System.out.println("idstruct:"+roomType.getId_structure());
        System.out.println("maxG:"+roomType.getMaxGuests());
        this.getStructureService().addPriceListsForRoomType(roomType.getId_structure(),roomType.getId() );
        return roomType;
    }
   
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public RoomType update(RoomType roomType) {        
        
    	this.getRoomTypeService().updateRoomType(roomType);
        return roomType;
    }
	
	public RoomTypeService getRoomTypeService() {
		return roomTypeService;
	}
	public void setRoomTypeService(RoomTypeService roomTypeService) {
		this.roomTypeService = roomTypeService;
	}
	public StructureService getStructureService() {
		return structureService;
	}
	public void setStructureService(StructureService structureService) {
		this.structureService = structureService;
	}
	public FacilityService getFacilityService() {
		return facilityService;
	}
	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	public ImageService getImageService() {
		return imageService;
	}
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
	
}