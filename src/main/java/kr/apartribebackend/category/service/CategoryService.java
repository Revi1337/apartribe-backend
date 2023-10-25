package kr.apartribebackend.category.service;

import kr.apartribebackend.apart.domain.Apartment;
import kr.apartribebackend.apart.exception.ApartNonExistsException;
import kr.apartribebackend.apart.repository.ApartmentRepository;
import kr.apartribebackend.category.domain.Category;
import kr.apartribebackend.category.dto.ArticleCategoryDto;
import kr.apartribebackend.category.dto.TogetherCategoryDto;
import kr.apartribebackend.category.exception.CategoryAlreadyExistsException;
import kr.apartribebackend.category.exception.ModifiedCategoryRequestException;
import kr.apartribebackend.category.repository.CategoryRepository;
import kr.apartribebackend.member.dto.MemberDto;
import kr.apartribebackend.category.exception.CategoryCantMakeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.apartribebackend.category.domain.CategoryTag.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ApartmentRepository apartmentRepository;

    @Transactional
    public Category addArticleCategory(final String apartId,
                                       final MemberDto memberDto,
                                       final ArticleCategoryDto articleCategoryDto) {
        return addCategory(memberDto, apartId, ARTICLE, articleCategoryDto.toEntity());
    }

    @Transactional
    public Category addTogetherCategory(final String apartId,
                                        final MemberDto memberDto,
                                        final TogetherCategoryDto togetherCategoryDto) {
        return addCategory(memberDto, apartId, TOGETHER, togetherCategoryDto.toEntity());
    }

    public Category addCategory(final MemberDto memberDto,
                                final String apartId,
                                final String categoryTag,
                                final Category category) {
        validMemberCanMakeCategory(memberDto, apartId);
        final Apartment apartment = apartmentRepository.findApartmentByCode(apartId)
                .orElseThrow(ApartNonExistsException::new);

        categoryRepository.findCategoryByTagAndNameWithApart(apartId, categoryTag, category.getName())
                .ifPresentOrElse(
                        c -> { throw new CategoryAlreadyExistsException(); },
                        () -> category.registApartment(apartment)
                );
        return categoryRepository.save(category);
    }

    public List<ArticleCategoryDto> listArticleCategory() {
        return listCategory(ARTICLE);
    }

    public List<ArticleCategoryDto> listTogetherCategory() {
        return listCategory(TOGETHER);
    }

    public List<ArticleCategoryDto> listCategory(final String categoryTag) {
        return categoryRepository.findCategoriesByTag(categoryTag).stream()
                .map(ArticleCategoryDto::from)
                .toList();
    }

    private void validMemberCanMakeCategory(MemberDto memberDto, String apartId) {
        if (!memberDto.isAuthenticated()) {
            throw new CategoryCantMakeException();
        }
        if (!memberDto.getApartCode().equals(apartId)) {
            throw new ModifiedCategoryRequestException();
        }
    }

}
