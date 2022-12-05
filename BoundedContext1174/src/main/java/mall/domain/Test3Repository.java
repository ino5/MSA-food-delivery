package mall.domain;

import mall.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="test3s", path="test3s")
public interface Test3Repository extends PagingAndSortingRepository<Test3, Long>{

}
