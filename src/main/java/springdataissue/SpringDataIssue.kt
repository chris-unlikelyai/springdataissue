package springdataissue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Table
data class ParentEntity(
  @MappedCollection val children: List<ChildEntity>,
  @Id val id: UUID? = null,
)

@Table
data class ChildEntity(@Id val id: UUID? = null)

interface EntityRepository : CrudRepository<ParentEntity, UUID>

@SpringBootApplication class SpringDataIssue

@Service
class Startup(private val entityRepository: EntityRepository) {
  @EventListener(ApplicationReadyEvent::class)
  fun onStartup() {
    val entity1 = entityRepository.save(ParentEntity(listOf(ChildEntity())))
    println("After initial save:            $entity1")

    val entity2 = entity1.copy(children = entity1.children + ChildEntity())
    println("Updated in memory:             $entity2")

    val entity3 = entityRepository.save(entity2)
    println("After saving updated entity:   $entity3")

    val entityE = entityRepository.findByIdOrNull(entity3.id!!)!!
    println("Updated entity loaded from DB: $entityE")
  }
}

fun main(args: Array<String>) {
  runApplication<SpringDataIssue>(*args)
}
