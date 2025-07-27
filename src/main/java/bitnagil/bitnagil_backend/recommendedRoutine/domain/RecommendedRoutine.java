package bitnagil.bitnagil_backend.recommendedRoutine.domain;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.Emotion;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineLevel;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecommendedRoutine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendedRoutineId;

    private String recommendedRoutineName; // 추천 루틴 이름(미션에 해당)

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private RecommendedRoutineType recommendedRoutineType; // 추천 루틴 타입 (분류에 해당)

    private LocalTime time; // 시간

    /**
     * 추천 루틴의 반복 요일은 코드레벨에서 설정한다.
     */

    private String recommendedRoutineDescription; // 추천 루틴 설명(목적에 해당)

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private RecommendedRoutineLevel recommendedRoutineLevel; // 추천 루틴 레벨 (난이도에 해당)

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private Emotion emotion; // 감정

    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case resultCase;

    // RecommendedRoutineDetail과 양방향 연관관계 설정
//    @OneToMany(mappedBy = "recommendedRoutine") // todo: cascade 옵션 추가 필요
//    private List<RecommendedSubRoutine> recommendedSubRoutineDetailSearchResult = new ArrayList<>();

    // 양방향 연관관계 편의 메서드
//    public void addRecommendedSubRoutine(RecommendedSubRoutine detail) {
//        this.recommendedSubRoutineDetailSearchResult.add(detail);
//        if(detail.getRecommendedRoutine() != this){
//            detail.setRecommendedRoutine(this);
//        }
//    }
}
